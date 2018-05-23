package com.zml.oa.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zml.oa.entity.BaseVO;
import com.zml.oa.entity.CommentVO;
import com.zml.oa.entity.Group;
import com.zml.oa.entity.Message;
import com.zml.oa.entity.Project;
import com.zml.oa.entity.User;
import com.zml.oa.entity.Vacation;
import com.zml.oa.entity.WorkOrder;
import com.zml.oa.pagination.Pagination;
import com.zml.oa.pagination.PaginationThreadUtils;
import com.zml.oa.service.IProcessService;
import com.zml.oa.service.IProjectService;
import com.zml.oa.service.IVacationService;
import com.zml.oa.service.IWorkOrderService;
import com.zml.oa.util.BeanUtils;
import com.zml.oa.util.UserUtil;

/**
 * @ClassName: WorkOrderAction
 * @Description:工单控制类,没有用动态任务分配
 * @author: chenli
 * @date: 2018-5-16 上午10:35:50
 *
 */

@Controller
@RequestMapping("/workOrderAction")
public class WorkOrderAction {
	private static final Logger logger = Logger.getLogger(WorkOrderAction.class);
	
	@Autowired
	private IVacationService vacationService;
	
	@Autowired
	private IWorkOrderService workOrderService;
	
	@Autowired
	protected RuntimeService runtimeService;
	
    @Autowired
    protected IdentityService identityService;
    
    @Autowired
    protected TaskService taskService;
    
	@Autowired
	private IProcessService processService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private IProjectService projectService;
	
	/**
	 * 查询某人的所有请假申请
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("user:vacation:list")
	@RequestMapping("/toList_page")
	public String toList(Model model) throws Exception{
		User user = UserUtil.getUserFromSession();
		List<Vacation> list = this.vacationService.toList(user.getId());
//		for(Vacation v : list){
//			if(BaseVO.APPROVAL_SUCCESS.equals(v.getStatus())){
//				Vacation vacation = (Vacation)this.historyService.createHistoricVariableInstanceQuery()
//					.processInstanceId(v.getProcessInstanceId()).variableName("entity");
//				
//			}
//		}
		Pagination pagination = PaginationThreadUtils.get();
		model.addAttribute("page", pagination.getPageStr());
		model.addAttribute("vacationList", list);
		return "vacation/list_vacation";
	}
	
	
	/**
	 * 以下是EasyUI的页面需求
	 * 
	 */
	
	
	/**
	 * 跳转添加页面
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@RequiresPermissions("user:workOrder:toAdd")
	@RequestMapping(value = "/toAdd")
	public String toAdd(Model model) throws Exception{
		List<Project> projectList = projectService.findByOnline();
		model.addAttribute("projectList", projectList);
		return "workOrder/add_workOrder";
	}	
	
	/**
     * 添加并启动工单流程
     *
     * @param workOrder
     */
	@RequiresPermissions("user:workOrder:doAdd")
	@RequestMapping(value = "/doAdd")
	@ResponseBody
	public Message doAdd(
			@ModelAttribute("workOrder") WorkOrder workOrder) throws Exception{
        User user = UserUtil.getUserFromSession();
        
        // 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
//        if (user == null || user.getId() == null) {
//        	model.addAttribute("msg", "登录超时，请重新登录!");
//            return "login";
//        }
        Message message = new Message();
        workOrder.setUser_id(user.getId());
        workOrder.setUser_name(user.getRealName());
        workOrder.setApplyUserId(user.getId());
        workOrder.setApplyUser(user.getRealName());
        workOrder.setApplyDate(new Date());
        workOrder.setStatus(BaseVO.PENDING);
        workOrder.setBusinessType(BaseVO.WORKORDER);
        workOrder.setTitle(user.getRealName()+"的工单");
        this.workOrderService.doAdd(workOrder);
        String businessKey = workOrder.getId().toString();
        workOrder.setBusinessKey(businessKey);
        try {
        	String processInstanceId = this.processService.startWordOrder(workOrder);
            message.setStatus(Boolean.TRUE);
			message.setMessage("工单流程已启动，流程ID：" + processInstanceId);
            logger.info("processInstanceId: "+processInstanceId);
        } catch (ActivitiException e) {
        	message.setStatus(Boolean.FALSE);
            if (e.getMessage().indexOf("no processes deployed with key") != -1) {
                logger.warn("没有部署流程!", e);
    			message.setMessage("没有部署流程，请联系系统管理员，在[流程定义]中部署相应流程文件！");
            } else {
                logger.error("启动请假流程失败：", e);
                message.setMessage("启动请假流程失败，系统内部错误！");
            }
            throw e;
        } catch (Exception e) {
            logger.error("启动工单流程失败：", e);
            message.setStatus(Boolean.FALSE);
            message.setMessage("启动工单流程失败，系统内部错误！");
            throw e;
        }
		return message;
	}
	/**
	 * url路由
	 * @param taskId
	 * @param model
	 * @param redirect
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/transferAction/{taskId}")
	public String transferAction(@PathVariable("taskId") String taskId, Model model, RedirectAttributes redirect) throws Exception{
		redirect.addFlashAttribute("taskId", taskId);
		//获取当前任务节点 Form KEY
		TaskFormData formData = formService.getTaskFormData(taskId);
		String formKey = formData.getFormKey();		
		return "redirect:"+formKey;
	}
	/**
	 * 业务部门审核数据显示
	 * @param taskId
	 * @param model
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	@RequestMapping("/businessAudit")
	public String businessAudit(@ModelAttribute("taskId") String taskId,Model model) throws NumberFormatException, Exception{
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		// 根据任务查询流程实例
    	String processInstanceId = task.getProcessInstanceId();
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		WorkOrder workOrder = (WorkOrder) this.runtimeService.getVariable(pi.getId(), "entity");
		//vacation.setTask(task);
		//vacation.setProcessInstanceId(processInstanceId);
		List<CommentVO> commentList = this.processService.getComments(processInstanceId);
		
		//获取当前任务节点 Form KEY
		TaskFormData formData = formService.getTaskFormData(taskId);
		List<Project> projectList = projectService.findByOnline();
		String formKey = formData.getFormKey();
		String result = "workOrder/business_audit";
		model.addAttribute("formKey", formKey);
		model.addAttribute("workOrder", workOrder);
		model.addAttribute("commentList", commentList);
		model.addAttribute("projectList",projectList);
    	return result;
	}
	
    /**
     * 业务部门审批
     * @param content
     * @param completeFlag
     * @param taskId
     * @param redirectAttributes
     * @param session
     * @return
     * @throws Exception
     */
	@RequiresPermissions("user:workOrder:complate")  //数据库中权限字符串为user:*:complate， 通配符*匹配到vacation所以有权限操作 
    @RequestMapping("/complate/{taskId}")
	@ResponseBody
    public Message complate(
    		@RequestParam("workOrderId") Integer workOrderId,
    		@RequestParam(value="content",required = false) String content,
    		@RequestParam(value="completeFlag",required = false) Boolean completeFlag,
    		@PathVariable("taskId") String taskId, 
    		RedirectAttributes redirectAttributes,
    		@ModelAttribute("workOrder") WorkOrder workOrder) throws Exception{
    	User user = UserUtil.getUserFromSession();
    	Message message = new Message();
    	try {
    		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
    		String processInstanceId = task.getProcessInstanceId();
    		//WorkOrder workOrder = this.workOrderService.findById(workOrderId);    		
    		WorkOrder baseWorkOrder = (WorkOrder) this.runtimeService.getVariable(processInstanceId, "entity");
    		Map<String, Object> variables = new HashMap<String, Object>();
    		String taskDefKey = task.getTaskDefinitionKey();
    		if("businessAudit".equals(taskDefKey)) {
	    		variables.put("isPass", completeFlag);
	    		baseWorkOrder.setBusinessAuditUserId(user.getId());
	    		baseWorkOrder.setBusinessAuditUser(user.getRealName());
	    		baseWorkOrder.setBusinessAuditDate(new Date());
	    		if(!completeFlag){
	    			baseWorkOrder.setTitle(baseWorkOrder.getUser_name()+" 的工单申请失败,需修改后重新提交！");
	    			baseWorkOrder.setStatus(BaseVO.APPROVAL_FAILED);    			
	    		}
	    		if(completeFlag){
	    			//此处需要修改，不能根据人来判断审批是否结束。应该根据流程实例id(processInstanceId)来判定。
	    			//判断指定ID的实例是否存在，如果结果为空，则代表流程结束，实例已被删除(移到历史库中)
	    			ProcessInstance pi = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
	    			if(BeanUtils.isBlank(pi)){
	    				baseWorkOrder.setStatus(BaseVO.APPROVAL_SUCCESS);
	    			}
	    		}
    		}else if("businessUpdate".equals(taskDefKey)) {
    			baseWorkOrder.setProject(workOrder.getProject());
    			baseWorkOrder.setDomain(workOrder.getDomain());
    			baseWorkOrder.setCoder(workOrder.getCoder());
    			baseWorkOrder.setCoderId(workOrder.getCoderId());
    			baseWorkOrder.setDevelopExplain(workOrder.getDevelopExplain());
    			baseWorkOrder.setStatus(BaseVO.PENDING);
    			baseWorkOrder.setTitle(baseWorkOrder.getUser_name()+" 的工单申请,修改后重新提交！");
    		}
    		variables.put("entity", baseWorkOrder);
    		// 完成任务
    		this.processService.complete(taskId, content, user.getId().toString(), variables);
    		this.workOrderService.doUpdate(baseWorkOrder);
			message.setStatus(Boolean.TRUE);
			message.setMessage("任务办理完成！");
		} catch (ActivitiObjectNotFoundException e) {
			message.setStatus(Boolean.FALSE);
			message.setMessage("此任务不存在，请联系管理员！");
			throw e;
		} catch (ActivitiException e) {
			message.setStatus(Boolean.FALSE);
			message.setMessage("此任务正在协办，您不能办理此任务！");
			throw e;
		} catch (Exception e) {
			message.setStatus(Boolean.FALSE);
			message.setMessage("任务办理失败，请联系管理员！");
			throw e;
		}
		return message;
    }
	
	/**
	 * 申请人修改
	 * @param taskId
	 * @param model
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	@RequestMapping("/businessUpdate")
	public String businessUpdate(@ModelAttribute("taskId") String taskId,Model model) throws NumberFormatException, Exception{
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		// 根据任务查询流程实例
    	String processInstanceId = task.getProcessInstanceId();
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		WorkOrder workOrder = (WorkOrder) this.runtimeService.getVariable(pi.getId(), "entity");
		//vacation.setTask(task);
		//vacation.setProcessInstanceId(processInstanceId);
		List<CommentVO> commentList = this.processService.getComments(processInstanceId);
		
		//获取当前任务节点 Form KEY
		TaskFormData formData = formService.getTaskFormData(taskId);
		List<Project> projectList = projectService.findByOnline();
		String formKey = formData.getFormKey();
		String result = "workOrder/edit_workOrder";
		model.addAttribute("formKey", formKey);
		model.addAttribute("workOrder", workOrder);
		model.addAttribute("commentList", commentList);
		model.addAttribute("projectList",projectList);
    	return result;
	}	
	
	
	/**
	 * 调整请假申请
	 * @param vacation
	 * @param taskId
	 * @param processInstanceId
	 * @param reApply
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("user:vacation:modify")
	@RequestMapping(value = "/modifyVacation/{taskId}", method = RequestMethod.POST)
	@ResponseBody
	public Message modifyVacation(
			@ModelAttribute("vacation") Vacation vacation,
			@PathVariable("taskId") String taskId,
			@RequestParam("processInstanceId") String processInstanceId,
			@RequestParam("reApply") Boolean reApply) throws Exception{
		
		User user = UserUtil.getUserFromSession();
		Message message = new Message();
		
        Map<String, Object> variables = new HashMap<String, Object>();
        vacation.setUserId(user.getId());
        vacation.setUser_name(user.getName());
        vacation.setBusinessType(BaseVO.VACATION);
        vacation.setApplyDate(new Date());
        vacation.setBusinessKey(vacation.getId().toString());
        vacation.setProcessInstanceId(processInstanceId);
        if(reApply){
        	//修改请假申请
	        vacation.setTitle(user.getName()+" 的请假申请！");
	        vacation.setStatus(BaseVO.PENDING);
	        //由userTask自动分配审批权限
//	        if(vacation.getDays() <= 3){
//            	variables.put("auditGroup", "manager");
//            }else{
//            	variables.put("auditGroup", "director");
//            }
	        message.setStatus(Boolean.TRUE);
			message.setMessage("任务办理完成，请假申请已重新提交！");
        }else{
        	vacation.setTitle(user.getName()+" 的请假申请已取消！");
        	vacation.setStatus(BaseVO.APPROVAL_FAILED);
	        message.setStatus(Boolean.TRUE);
			message.setMessage("任务办理完成，已经取消您的请假申请！");
        }
        try {
			this.vacationService.doUpdate(vacation);
			variables.put("entity", vacation);
			variables.put("reApply", reApply);
			this.processService.complete(taskId, "取消申请", user.getId().toString(), variables);
			
		} catch (ActivitiObjectNotFoundException e) {
			message.setStatus(Boolean.FALSE);
			message.setMessage("此任务不存在，请联系管理员！");
			throw e;
		} catch (ActivitiException e) {
			message.setStatus(Boolean.FALSE);
			message.setMessage("此任务正在协办，您不能办理此任务！");
			throw e;
		} catch (Exception e) {
			message.setStatus(Boolean.FALSE);
			message.setMessage("任务办理失败，请联系管理员！");
			throw e;
		}
		
    	return message;
    }
	
	/**
	 * 详细信息
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("user:vacation:details")
	@RequestMapping(value="/details/{id}")
	public String details(@PathVariable("id") Integer id, Model model) throws Exception{
		Vacation vacation = this.vacationService.findById(id);
		model.addAttribute("vacation", vacation);
		return "/vacation/details_vacation";
	}
	
}
