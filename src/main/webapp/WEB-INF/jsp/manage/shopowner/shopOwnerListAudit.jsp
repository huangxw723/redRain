﻿<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	<base href="<%=basePath%>">
	<!-- jsp文件头和头部 -->
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	
	<meta name="description" content="overview & stats" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<!-- basic styles -->
	<link href="static/css/bootstrap.min.css" rel="stylesheet" />
	<link href="static/css/bootstrap-responsive.min.css" rel="stylesheet" />
	<link rel="stylesheet" href="static/css/font-awesome.min.css" />
	<!-- page specific plugin styles -->
	<!-- 下拉框-->
	<link rel="stylesheet" href="static/css/chosen.css" />
	<!-- ace styles -->
	<link rel="stylesheet" href="static/css/ace.min.css" />
	<link rel="stylesheet" href="static/css/ace-responsive.min.css" />
	<link rel="stylesheet" href="static/css/ace-skins.min.css" />
	<%--<link rel="stylesheet" href="static/css/datepicker.css" />--%><!-- 日期框 -->
	<link rel="stylesheet" href="static/css/bootstrap-datetimepicker.min.css" /><!-- 日期框 -->

</head> 
<body>
		
<div class="container-fluid" id="main-container">


<div id="page-content" class="clearfix">
						
  <div class="row-fluid">


	<div class="row-fluid">
	
			<!-- 查询  -->
			<form action="shopowner/getShopOwnerListAudit.do" method="post" name="usersForm" id="usersForm">
			<input type="hidden" id="shopId" value="${shopId}"/>
			<input type="hidden" id="shop_owner_info_id" value="${shopOwnerInfoId}"/>  			
		<table>
        <tr>          						         
	       申请时间:<span>       
              <input class="span10 date-picker" name="startTime" id="startTime" type="text" value="${pd.startTime}" data-date-format="yyyy-mm-dd hh:ii:ss" style="width:220px;" />
                          至 <input class="span10 date-picker" name="endTime" id="endTime" type="text" value="${pd.endTime}"data-date-format="yyyy-mm-dd hh:ii:ss" style="width:220px;"  />                      
          </span>
            <span>

             <span class="input-icon"> 
             	<input autocomplete="off" id="nav-search-input" type="text" placeholder="请输入：商户名称/申请人/用户名"
										name="keyWord" value="${pd.keyWord}" style="width:250px;"/>
				 <i  class="icon-search"></i>
			  </span>
				&nbsp;
			  <button class="btn btn-mini btn-light" onclick="search();" style="vertical-align:top;" title="检索">
								<i id="nav-search-icon" class="icon-search"></i>
			  </button>			
          </span>
          
        </tr>
     
			</table>
			</form>
			<!-- 检索  -->


			<table id="table_report" class="table table-striped table-bordered table-hover">

				<thead>
					<tr>	
					  <th class="center">
                    	<label>
                      	<input type="checkbox" id="zcheckbox" />
                      	<span class="lbl"></span>
                    		</label>
                  		</th>					
						<th class="center">商户名称</th>						
						<th class="center">申请人</th>
						<th class="center">用户名</th>
						<th class="center">状态</th>
						<th class="center">申请时间</th>
                        <th class="center">操作</th>

					</tr>
				</thead>

				<tbody>

				<!-- 开始循环 -->

				
				<c:choose>
					<c:when test="${not empty list}">
						<c:forEach items="${list}" var="var" varStatus="vs">						
							<tr>						
							   <td class='center' style="width: 30px;">
                                  <label>
                                    <input type='checkbox' name='ids' value="${var.shopOwnerInfoId}" />
                                    <span class="lbl"></span>
                                  </label>
                                </td>		
								<td class="center">${var.shopName}</td>																																
								<td class="center">${var.applicant}</td>
								<td class="center">${var.account}</td>																
								<td class="center">
								    <c:if test="${var.auditStatus == 0}">
										<span>待审核</span>
									</c:if>
								</td>
								<td class="center">${var.createTime}</td>
								<td class="center">
								<a href="javascript:void(0);" onclick="query(${var.shopId},${var.shopOwnerInfoId})">查看</a>
								<a href="javascript:void(0);" onclick="updateAuditStatus(${var.shopId},${var.shopOwnerInfoId})">审批</a>
								</td>								
							</tr>

						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr class="main_info">
							<td colspan="100" class="center" >没有相关数据</td>
						</tr>
					</c:otherwise>
				</c:choose>


				</tbody>								
			</table>				
			<div class="page-header position-relative">
					<table style="width: 100%;">
						<tr>
						 	<td style="vertical-align:top;">
								<a class="btn btn-mini btn-success" id="" onclick="allAuditStatus('确定要审批通过选中的数据吗?')" style="width: 55px; height: 27px; line-height:27px">批量通过</a>
							</td>
						
							<td style="vertical-align: top;">
								<div class="pagination"
									style="float: right; padding-top: 0px; margin-top: 0px;">${page.pageStr}</div>
							</td>
						</tr>
					</table>
			</div>
		
	</div>
 
 
 
 
	<!-- PAGE CONTENT ENDS HERE -->
  </div><!--/row-->
	
</div><!--/#page-content-->
</div><!--/.fluid-container#main-container-->
		
		<!-- 返回顶部  -->
		<a href="#" id="btn-scroll-up" class="btn btn-small btn-inverse">
			<i class="icon-double-angle-up icon-only"></i>
		</a>
		
		<!-- 引入 -->
		<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
		<script src="static/js/bootstrap.min.js"></script>
		<script src="static/js/ace-elements.min.js"></script>
		<script src="static/js/ace.min.js"></script>
		
		<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
		<%--<script type="text/javascript" src="static/js/bootstrap-datepicker.min.js"></script>--%><!-- 日期框 -->
		<script type="text/javascript" src="static/js/bootstrap-datetimepicker.js"></script><!-- 日期框 -->
		<script type="text/javascript" src="static/js/bootstrap-datetimepicker.zh-CN.js"></script><!-- 日期框 -->
		<script type="text/javascript" src="static/js/bootbox.min.js"></script><!-- 确认窗口 -->
		<!-- 引入 -->

		<!--引入弹窗组件start-->
		<script type="text/javascript" src="plugins/attention/zDialog/zDrag.js"></script>
		<script type="text/javascript" src="plugins/attention/zDialog/zDialog.js"></script>
		<!--引入弹窗组件end-->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script><!--提示框-->
		<script type="text/javascript">
				
			$(function() {
                //日期框
                //$('.date-picker').datepicker();

                //复选框
                $('table th input:checkbox').on('click' , function(){
                    var that = this;
                    $(this).closest('table').find('tr > td:first-child input:checkbox')
                        .each(function(){
                            this.checked = that.checked;
                            $(this).closest('tr').toggleClass('selected');
                        });

                });

                $('#startTime').datetimepicker({
                    minuteStep :1,
                    language:  'zh-CN',
                    autoclose: 1,
                    format: "yyyy-mm-dd hh:ii:00",
                }).on('changeDate',function(e){
                    var startTime = e.date;
                    $('#endTime').datetimepicker('setStartDate',startTime);
                });

                $('#endTime').datetimepicker({
                    minuteStep :1,
                    language:  'zh-CN',
                    autoclose : 1,
                    format: "yyyy-mm-dd hh:ii:00",
                }).on('changeDate',function(e){
                    var endTime = e.date;
                    $('#startTime').datetimepicker('setEndDate',endTime);
                });

            });
		</script>
		
												
		<script type="text/javascript">			
		 $(top.hangge());
		 
		//检索
			function search(){
				top.jzts();
				var form = $("#usersForm");
				form.find('#currentPage').attr('name','currentPage');
				form.find('#showCount').attr('name','showCount');
				$("#userForm").submit();
			}
		
		//送审
		function updateAuditStatus(shopId,shopOwnerId){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="审核处理";
			 diag.URL = '<%=basePath%>shopowner/toshopInfoListPage.do?action=1&status=2&shopId='+shopId+'&shopOwnerId='+shopOwnerId;
			 diag.Width = 1500;
			 diag.Height = 800;
			 diag.CancelEvent = function(){ //关闭事件				 	
				diag.close();
				history.go(0);
			 };
			 diag.show();
		}
		
		//查看
		function query(shopId,shopOwnerId){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="详细信息";
			 diag.URL = '<%=basePath%>shopowner/toshopInfoListPage.do?action=4&shopId='+shopId+'&shopOwnerId='+shopOwnerId;
			 diag.Width = 1500;
			 diag.Height = 800;
			 diag.CancelEvent = function(){ //关闭事件
				diag.close();
			 };
			 diag.show();
		}
		
	   //批量审批处理		
	   function allAuditStatus(msg){
			  bootbox.confirm(msg, function(result) {
					if(result) {
					  var str = '';
				        for(var i=0;i < document.getElementsByName('ids').length;i++)
				        {
				          if(document.getElementsByName('ids')[i].checked){
				            if(str==''){
				            	str += document.getElementsByName('ids')[i].value;
				            	}
				            else
				           	 { 
				            	str += ',' + document.getElementsByName('ids')[i].value;
				            	}
				          }
				        }
		
		        if(str==''){
		        	bootbox.dialog("您没有选择任何内容!", 
							[
							  {
								"label" : "关闭",
								"class" : "btn-small btn-success",
								"callback": function() {
									//Example.show("great success");
									}
								}
							 ]
						);
						
						$("#zcheckbox").tips({
							side:3,
				            msg:'点这里全选',
				            bg:'#AE81FF',
				            time:8
				        });
						
						return;
		        }else{
		        	if(msg == '确定要审批通过选中的数据吗?'){
						top.jzts();
						  $.ajax({
				   			    type : "post",
				   		        url : "shopowner/allAuditStatus",
				   		        dataType : "json",
				   				async: false,
				   				data : {
				   					ids: str,
				   					status:'2',
				   				},
				   		        success : function (data) {
				   		        	if("0"==data.status){
				   		        		var errIds = data.errIds;
				   		        		if(""==errIds){
					   		        		setTimeout(window.location.href="shopowner/getShopOwnerListAudit",100);
					   		        		return;
				   		        		}else{
				   		        			alert("审批完成！其中审批失败的审批编号有"+errIds);	
											setTimeout(window.location.href="shopowner/getShopOwnerListAudit",100);
					   		        		return;
				   		        		}
				   		    			}else{
				   		    				alert("审批失败")
				   		    			setTimeout(window.location.href="shopowner/getShopOwnerListAudit",100);
				   			    			return;
				   		    			}
				   		        }
				   		});
					}
		        }
			 }
		  });
	    };
		</script>
	</body>
</html>

