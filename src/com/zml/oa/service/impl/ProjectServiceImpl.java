package com.zml.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zml.oa.entity.BaseVO;
import com.zml.oa.entity.Project;
import com.zml.oa.pagination.Page;
import com.zml.oa.service.IBaseService;
import com.zml.oa.service.IProjectService;

@Service
public class ProjectServiceImpl extends BaseServiceImpl<Project>implements IProjectService {
	@Autowired 
	private IBaseService<Project> baseService;
	@Override
	public List<Project> findByOnline() throws Exception {
		return baseService.findByWhere("Project", new String[] {"status"}, new String[] {BaseVO.PROJECT_STATUS_ONLINE});
	}
	@Override
	public Project getProjectById(Integer id) throws Exception {
		return baseService.getUnique("Project", new String[] {"id"}, new String[] {id.toString()});
	}
	@Override
	public List<Project> getProjectList(Page<Project> page,String[] columns, String[] values, String sort, String order) throws Exception {		
		return getListPage("Project", columns, values, page, sort, order);		
	}

	public void doAdd(Project project) throws Exception {
		add(project);
	}
}
