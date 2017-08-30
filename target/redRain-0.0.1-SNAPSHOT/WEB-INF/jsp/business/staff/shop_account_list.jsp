﻿<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.aoshi.util.PropertyUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String httpUrl = PropertyUtils.getHTTP_URL();
%>
<!DOCTYPE html>
<html lang="en">
			<base href="<%=basePath%>">
		<meta charset="utf-8" />
		<title></title>
		<meta name="description" content="overview & stats" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<link href="static/css/bootstrap.min.css" rel="stylesheet" />
		<link href="static/css/bootstrap-responsive.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="static/css/font-awesome.min.css" />
		<!-- 下拉框 -->
		<link rel="stylesheet" href="static/css/chosen.css" />
		<link rel="stylesheet" href="static/css/ace.min.css" />
		<link rel="stylesheet" href="static/css/ace-responsive.min.css" />
		<link rel="stylesheet" href="static/css/ace-skins.min.css" />
		<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
		<%--<link rel="stylesheet" href="static/css/datepicker.css" />--%><!-- 日期框 -->
		<link rel="stylesheet" href="static/css/bootstrap-datetimepicker.min.css" /><!-- 日期框 -->
		<!--引入弹窗组件start-->
		<script type="text/javascript" src="plugins/attention/zDialog/zDrag.js"></script>
		<script type="text/javascript" src="plugins/attention/zDialog/zDialog.js"></script>
		<!--引入弹窗组件end-->
		<!--提示框-->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<script type="text/javascript" src="static/js/common/commonutils.js"></script>
<body>
		
<div class="container-fluid" id="main-container">


<div id="page-content" class="clearfix">
						
  <div class="row-fluid">

	<div class="row-fluid">

			<!-- 检索  -->
			<form action="<%=basePath%>staff/listAll" method="post" name="usersForm" id="usersForm">
			<input type="hidden" id="currentPage" value="${pd.currentPage}"/>
			<input type="hidden" id="showCount" value="${pd.showCount}"/>
			<table>

        <tr>          
             用户状态:<span>
          
             <select name="status" id="status" style="vertical-align:top;width: 90px">
             <option value="">全部</option>
             <option value="0"<c:if test="${pd.status=='0' }">selected="selected" </c:if> >启用</option>
             <option value="1"<c:if test="${pd.status=='1' }">selected="selected"</c:if>>停用</option>
             </select>
          </span>
          
		创建时间:<span>
        
             <input class="span10 date-picker" name="startTime" id="startTime" type="text" value="${pd.startTime}" data-date-format="yyyy-mm-dd hh:ii:ss" style="width:220px;" placeholder=""/>
                          至 <input class="span10 date-picker" name="endTime" id="endTime" type="text" value="${pd.endTime}" data-date-format="yyyy-mm-dd hh:ii:ss" style="width:220px;" placeholder=""/>
            </span>
        <span class="input-icon"> 
             	<input autocomplete="off" id="nav-search-input" type="text" name="keyWord" value="${pd.keyWord}" style="width:250px;" placeholder="用户名" >
				<i  class="icon-search"></i>
				&nbsp;
				<button class="btn btn-mini btn-light" onclick="searchInfo();" style="vertical-align:top;" title="检索">
				<i id="nav-search-icon" class="icon-search"></i>
			    </button>
				&nbsp;
				<a class="btn btn-small btn-success"  style="vertical-align:top;"  onclick="add('1');">新增</a>
			  </span>
          
        </tr>
     
			</table>
			</form>
			<!-- 检索  -->


			<table id="table_report" class="table table-striped table-bordered table-hover">

				<thead>
					<tr>
<!-- 						<th class="center">帐号类型</th> -->
						<th class="center">商户名称</th>
						<th class="center">用户名</th>
						<th class="center">创建时间</th>
						<th class="center">用户状态</th>
                        <th class="center">操作</th>

					</tr>
				</thead>

				<tbody>

				<!-- 开始循环 -->

				
				<c:choose>
					<c:when test="${not empty list}">
						<c:forEach items="${list}" var="var" varStatus="vs">
							<tr>
								<%-- <td class="center">
									<c:if test="${var.type == 1}">
										店铺管理员
									</c:if>
									<c:if test="${var.type == 2}">
										员工
									</c:if>
								</td> --%>
								<td class="center">${var.shopName}</td>
								<td class="center">${var.account}</td>
								<td class="center">${var.createTime}</td>
									<td class="center">
										<c:if test="${var.status ==0}">
											启用
										</c:if>
										<c:if test="${var.status ==1}">
											停用
										</c:if>
									</td>
								<td class="center">
                                        <div class="inline position-relative">
									
										<c:if test="${var.status == 0}">
											<a href="javascript:void(0);" onclick="updateUserStatus(${var.userId},'${var.account}',1)">停用</a>
										</c:if>
										<c:if test="${var.status == 1}">
											<a href="javascript:void(0);" onclick="updateUserStatus(${var.userId},'${var.account}',0)">启用</a>
										</c:if>
										<c:if test="${var.account ne asUser.account}">
											<a href="javascript:void(0);" onclick="idelete(${var.userId},'${var.account}')">删除</a>
										</c:if>
										</div>
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
									
									<td style="vertical-align: top;">
										<div class="pagination"
											style="float: right; padding-top: 0px; margin-top: 0px;">${page.pageStr}</div>
									</td>
								</tr>
							</table>
						</div>
	</div>
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
		<!-- 确认窗口 -->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
		<script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
		<!-- 引入 -->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script><!--提示框-->
		<script type="text/javascript">
		
		$(top.hangge());
		
		//检索
		function searchInfo(){
			top.jzts();
			var form = $("#usersForm");
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			$("#usersForm").submit();
		}

	    $(function () {
	        //单选框
	        $(".chzn-select").chosen();
	        $(".chzn-select-deselect").chosen({allow_single_deselect: true});
	        $(".fancybox").fancybox({
	            helpers: {
	                title: {
	                    type: 'inside'
	                }
	            }
	        });
	    });
	    

		//更新状态
		function updateUserStatus(id,name,status){
			if(status==1){
				var temp = '停用';
			}else if(status==0) {
				var temp = '启用';
			}
			var form = $("#usersForm");
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			bootbox.confirm("确定要"+temp+"该用户【" + name + "】吗?", function (result) {
				if (result) {
					$.ajax({
						type: "POST",
						url: "<%=basePath%>account/updateStatus.do",
						data: {userId:id,status:status},
						dataType: 'json',
						success: function (data) {
							if (data.status != 0) {
								alert(data.msg);
								return;
							}
							alert("操作成功")
							form.submit();
						},
						error:function () {
							alert("系统繁忙！");
						}
					});
				}
			});
		}
	
		//新增
		function add(){
			
			var form = $("#usersForm");
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="新增";
			 diag.URL = '<%=basePath%>staff/saveShopAccountPage?action='+1;
			 diag.Width = 840;
			 diag.Height = 650;
			 diag.CancelEvent = function () { //关闭事件
		            
		            diag.close();
		            form.submit()
		        };
			 
			 diag.show();
		}
		

		//删除
		function idelete(id,name){
			var form = $("#usersForm");
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			bootbox.confirm("确定要删除该用户【" + name + "】吗?", function (result) {
				if (result) {
					$.ajax({
						type: "POST",
						url: "<%=basePath%>account/delete.do",
						data: {userId:id},
						dataType: 'json',
						success: function (data) {
							if (data.status != 0) {
								alert(data.msg);
								return;
							}
							alert("操作成功")
							form.submit();
						},
						error:function () {
							alert("系统繁忙！");
						}
					});
				}
			});
		}
		

		</script>
		
		<script type="text/javascript">
		
		$(function() {
			
			//下拉框
			$(".chzn-select").chosen(); 
			$(".chzn-select-deselect").chosen({allow_single_deselect:true}); 

			//复选框
			$('table th input:checkbox').on('click' , function(){
				var that = this;
				$(this).closest('table').find('tr > td:first-child input:checkbox')
				.each(function(){
					this.checked = that.checked;
					$(this).closest('tr').toggleClass('selected');
				});
					
			});
			
			
			/*$('#startTime').datepicker({
			    todayBtn : "linked",  
			    autoclose : true,  
			    todayHighlight : true,  
			    endDate : new Date()  
			}).on('changeDate',function(e){  
			    var startTime = e.date;  
			    $('#endTime').datepicker('setStartDate',startTime);  
			});  
			//结束时间：  
			$('#endTime').datepicker({  
			    todayBtn : "linked",  
			    autoclose : true,  
			    todayHighlight : true,   
			}).on('changeDate',function(e){  
			    var endTime = e.date;  
			    $('#startTime').datepicker('setEndDate',endTime);  
			});*/
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
		
	</body>
</html>

