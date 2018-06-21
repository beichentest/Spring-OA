package com.zml.oa.service;

import java.io.Serializable;
import java.util.List;

import com.zml.oa.entity.Project;
import com.zml.oa.entity.WorkOrder;
import com.zml.oa.pagination.Page;

public interface IProjectService {
	public List<Project> findByOnline()throws Exception; 
	
	public Project getProjectById(Integer id)throws Exception;

	public List<Project> getProjectList(Page<Project> page,String[] columns, String[] values, String sort, String order) throws Exception;

	public void doAdd(Project project)throws Exception;
}
