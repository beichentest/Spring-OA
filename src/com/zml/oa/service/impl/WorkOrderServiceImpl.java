package com.zml.oa.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zml.oa.entity.Vacation;
import com.zml.oa.entity.WorkOrder;
import com.zml.oa.pagination.Page;
import com.zml.oa.service.IBaseService;
import com.zml.oa.service.IWorkOrderService;

@Service
public class WorkOrderServiceImpl implements IWorkOrderService {
	@Autowired 
	private IBaseService<WorkOrder> baseService;
	
	@Override
	public Serializable doAdd(WorkOrder workOrder) throws Exception {
		return this.baseService.add(workOrder);
	}

	@Override
	public void doUpdate(WorkOrder workOrder) throws Exception {
		this.baseService.update(workOrder);
	}

	@Override
	public void doDelete(WorkOrder workOrder) throws Exception {
		this.baseService.delete(workOrder);
	}

	@Override
	public List<WorkOrder> toList(Integer userId) throws Exception {
		return this.baseService.findByPage("WorkOrder", new String[] {"applyUserId"}, new String[] {userId.toString()});
	}

	@Override
	public WorkOrder findById(Integer id) throws Exception {
		return this.baseService.getUnique("WorkOrder", new String[]{"id"}, new String[]{id.toString()});
	}

	@Override
	public List<WorkOrder> findByStatus(Integer userId, String status, Page<WorkOrder> page) throws Exception {
		List<WorkOrder> list = this.baseService.getListPage("WorkOrder", new String[]{"applyUserId","status"}, new String[]{userId.toString(), status}, page);
		return list;
	}

}
