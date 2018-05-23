package com.zml.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zml.oa.entity.BaseVO;
import com.zml.oa.entity.Project;
import com.zml.oa.service.IBaseService;
import com.zml.oa.service.IProjectService;

@Service
public class ProjectServiceImpl implements IProjectService {
	@Autowired 
	private IBaseService<Project> baseService;
	@Override
	public List<Project> findByOnline() throws Exception {
		return baseService.findByWhere("Project", new String[] {"status"}, new String[] {BaseVO.PROJECT_STATUS_ONLINE});
	}

}
