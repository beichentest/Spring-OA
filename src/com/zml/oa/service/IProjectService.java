package com.zml.oa.service;

import java.io.Serializable;
import java.util.List;

import com.zml.oa.entity.Project;
import com.zml.oa.entity.WorkOrder;
import com.zml.oa.pagination.Page;

public interface IProjectService {
	public List<Project> findByOnline()throws Exception; 
}
