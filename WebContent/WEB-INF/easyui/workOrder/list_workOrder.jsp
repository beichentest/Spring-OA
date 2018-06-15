<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/taglibs/taglibs.jsp"%>

<!DOCTYPE html>
<html>
  <head>
    <title>工单查看</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <body>
	<div class="well well-small" style="margin-left: 5px;margin-top: 5px">		
		<p>
			
		</p>
	</div>
	
	<div id="toolbar" style="padding:2px 0">
		<span>编号:</span>
		<input id="id" style="line-height:26px;border:1px solid #ccc">
		<span>申请人:</span>
		<input id="applyUser" style="line-height:26px;border:1px solid #ccc">
		<a href="#" class="easyui-linkbutton" class="easyui-linkbutton" plain="true" iconCls="icon-search" onclick="doSearch()">查询</a>
		<%-- <table cellpadding="0" cellspacing="0">
			<tr>
				<td style="padding-left:2px">
					<shiro:hasRole name="admin">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="showUser();">添加</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="edit();">编辑</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="del();">删除</a>|
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-reload" plain="true" onclick="sync();">同步用户</a>
					</shiro:hasRole>
			</tr>
		</table> --%>
	</div>
	<table id="workOrder_datagrid" title="工单查看" toolbar="#toolbar"></table>
  </body>
  <script type="text/javascript">
  $(function() {
		//数据列表
	    user_datagrid = $('#workOrder_datagrid').datagrid({
	        url: ctx+"/workOrderAction/toList",
	        width : 'auto',
			height :  $(this).height()-85,
			pagination:true,
			rownumbers:true,
			border:false,
			singleSelect:true,
			striped:true,
	        columns : [ 
	            [ 
	              {field : 'id',title : '编号',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},	
	              {field : 'project.name',title : '项目名称',width : fixWidth(0.2),align : 'left',sortable: true, 
	            	  formatter:function(value, row){
	                		return row.project.name;
	                	}  
	              },
	              {field : 'applyUser',title : '申请人',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},
	              {field : 'businessAuditUser',title : '审核人',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},
	              {field : 'coder',title : '开发人',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},
	              {field : 'coderAudit',title : '开发审核',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},
	              {field : 'tester',title : '测试人',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},
	              {field : 'testerAudit',title : '测试审核',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},
	              {field : 'webMaster',title : '运维人',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},
	              {field : 'webMasterAudit',title : '运维审核',width : fixWidth(0.05),align : 'left',sortable: true, editor : {type:'validatebox',options:{required:true}}},
	              {field : 'applyDate',title : '申请时间',width : fixWidth(0.2),align : 'left',sortable: true,
	            	  formatter:function(value,row){
							return moment(value).format("YYYY-MM-DD HH:mm:ss");
						}  
	              },
	              {field : 'verifyDate',title : '确认时间',width : fixWidth(0.2),align : 'left',sortable: true, 
	            	  formatter:function(value,row){
	            		  	if(value==''||value==null)
	            		  		return "";
							return moment(value).format("YYYY-MM-DD HH:mm:ss");
						}
	              },
	              {field : 'down',title : '下载',width : fixWidth(0.05),align : 'left', 
	            	  formatter:function(value,row){
	            		  return "<a class='trace' onclick=\"downloadWord('"+row.id+"')\" id='download' href='#'  title='下载'>下载</a>";
 					  }
	              }
	    	    ] 
	        ],
	        toolbar: "#toolbar"
	    });
	    
	    //搜索框
	/*    $("#searchbox").searchbox({ 
	    	menu:"#mm", 
	    	prompt :'模糊查询',
	    	searcher:function(value,name){   
	    		var str="{\"searchName\":\""+name+"\",\"searchValue\":\""+value+"\"}";
	    		var obj = eval('('+str+')');
	    		$dg.datagrid('reload',obj); 
	    	}
	    
	    });*/
	    
	    //修正宽高
		function fixHeight(percent)   
		{   
			return parseInt($(this).width() * percent);
			//return (document.body.clientHeight) * percent ;    
		}

		function fixWidth(percent)   
		{   
			return parseInt(($(this).width() - 30) * percent);
			//return (document.body.clientWidth - 50) * percent ;    
		}
	});
  	function downloadWord(id){
  		window.location.href = "downloadWord?id="+id
  	}
  	function doSearch (){
  		$('#workOrder_datagrid').datagrid('load',{
  			id: $('#id').val(),
  			applyUser: $('#applyUser').val()
  		});
  	}
  </script>
</html>
