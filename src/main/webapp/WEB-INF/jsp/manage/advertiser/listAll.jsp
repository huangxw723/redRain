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
	<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
	<link rel="stylesheet" href="static/css/datepicker.css" /><!-- 日期框 -->
	<!--引入弹窗组件start-->
	<script type="text/javascript" src="plugins/attention/zDialog/zDrag.js"></script>
	<script type="text/javascript" src="plugins/attention/zDialog/zDialog.js"></script>
	<!--引入弹窗组件end-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
</head> 
<body>
		
<div class="container-fluid" id="main-container">


<div id="page-content" class="clearfix">
						
  <div class="row-fluid">


	<div class="row-fluid">
	
			<!-- 检索  -->
			<form action="advertiser/listAll.do" method="post" name="usersForm" id="usersForm">
			<input type="hidden" id="currentPage" value="${pd.currentPage}"/>
			<input type="hidden" id="showCount" value="${pd.showCount}"/>
				<table>
					<tr>
								<td>
								联系人:
								<span class="input-icon">
									<input autocomplete="off" id="nav-search-input" type="text" name="contactName" value="${pd.contactName}" placeholder="这里输入关键词" style="width:220px;"/>
										<i  class="icon-search"></i>
								</span>
								</td><td>
								联系电话:
								<span class="input-icon">
									<input autocomplete="off" id="nav-search-input" type="text" name="contactPhone" value="${pd.contactPhone}" placeholder="这里输入关键词" style="width:220px;"/>
										<i  class="icon-search"></i>
								</span>
								</td>
								<td style="vertical-align: top;">
										&nbsp;
										<button class="btn btn-mini btn-light" onclick="search();"
											title="查询">
											<i id="nav-search-icon" class="icon-search"></i>
										</button>
										 
								</td>

							</tr>		
				</table>		
			</form>
			<!-- 检索  -->
			<table id="table_report" class="table table-striped table-bordered table-hover">
				
				<thead>
					<tr>
						<th class="center">序号</th>
						<th style="text-align:center;">联系人</th>
						<th style="text-align:center;">联系电话</th>
						<th style="text-align:center;">微信昵称</th>
						<th style="text-align:center;">公司名称</th>
						<th style="text-align:center;">公司地址</th>
						<th style="text-align:center;">来源</th>
						<th style="text-align:center;">操作</th>
					</tr>
				</thead>
										
				<tbody>
					
				<!-- 开始循环 -->	
				<c:choose>
					<c:when test="${not empty list}">
						<c:forEach items="${list}" var="var" varStatus="vs">
							<tr>
								<td class='center' style="width: 30px;">${vs.index+1}</td>
								<td class='center' style="text-align:center;">${var.contactName}</td>
								<td class='center' style="text-align:center;">${var.contactPhone}</td>
								<td class='center' style="text-align:center;">${var.wecharNickname}</td>
								<td class='center' style="text-align:center;">${var.companyName}</td>
								<td class='center' style="text-align:center;">${var.companyAddress}</td>
								<td class='center' style="text-align:center;">
									<c:if test="${var.source ==1}">扫码</c:if>
								</td>
								<td class='center'  style="text-align:center;">
										<a href="javascript:void(0);" onclick="query(${var.advertiserId})">详情</a>
								</td>
							</tr>
						</c:forEach>
						
					</c:when>
					<c:otherwise>
						<tr class="main_info">
							<td colspan="10" class="center">没有相关数据</td>
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
		<script type="text/javascript" src="static/js/bootstrap-datepicker.min.js"></script><!-- 日期框 -->
		<script type="text/javascript" src="static/js/bootbox.min.js"></script><!-- 确认窗口 -->
		<!-- 引入 -->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script><!--提示框-->
		<link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
		<script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>

		<script type="text/javascript">
		
		$(top.hangge());
		
		//检索
		function search(){
			top.jzts();
			var form = $("#usersForm");
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			$("#usersForm").submit();
		}
		
		//查看
		function query(id){
			
			var form = $("#usersForm");
			var parems = 'action=4'+ '&advertiserId='+id ;
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="详情";
			 diag.URL = '<%=basePath%>advertiser/page.do?'+parems;
			 diag.Width = 1150;
			 diag.Height = 510;
			 diag.CancelEvent = function(){ //关闭事件
				diag.close();
			 };
			 diag.show();
		}

		//删除
		function updatePrizeStatus(id,sn,name,status){
			var form = $('#usersForm')
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			if(status==5){
			bootbox.confirm("确定要删除并取消发布该礼品【"+name+" - ("+sn+")】吗?",function(result){
				if(result) {
					$.ajax({
						type: "POST",
						url: "<%=basePath%>prizeconfig/updatePrizeStatus.do",
				    	data: {prizeConfigId:id,status:status},
						dataType:'json',
						success: function(data){
							if(data.status != 0){
								alert(data.msg);
								return;
							}
								history.go(0);
						}
					});
				}
			});	
			}else if(status==0){//重发
				bootbox.confirm("确定要重新发布发布该礼品【"+name+" - ("+sn+")】吗?",function(result){
					if(result) {
						$.ajax({
							type: "POST",
							url: "<%=basePath%>prizeconfig/updatePrizeStatus.do",
					    	data: {"prizeConfigId":id,"status":status},
							dataType:'json',
							success: function(data){
								if(data.status != 0){
									alert(data.msg);
									return;
								}
									history.go(0);
							}
						});
					}
				});
			}else if(status==2){//禁用
				bootbox.confirm("确定要禁用并取消该礼品【"+name+" - ("+sn+")】吗?",function(result){
					if(result) {
						$.ajax({
							type: "POST",
							url: "<%=basePath%>prizeconfig/updatePrizeStatus.do",
					    	data: {"prizeConfigId":id,"status":status},
							dataType:'json',
							success: function(data){
								if(data.status != 0){
									alert(data.msg);
									return;
								}
									history.go(0);
							}
						});
					}
				});
			}else if(status==1){//启用
				bootbox.confirm("确定要启用该礼品【"+name+" - ("+sn+")】吗?",function(result){
					if(result) {
						$.ajax({
							type: "POST",
							url: "<%=basePath%>prizeconfig/updatePrizeStatus.do",
					    	data: {"prizeConfigId":id,"status":status},
							dataType:'json',
							success: function(data){
								if(data.status != 0){
									alert(data.msg);
									return;
								}
									history.go(0);
							}
						});
					}
				});
			}
			
		};

		</script>
		
		<script type="text/javascript">
			$(function() {
				//日期框
				$('.date-picker').datepicker();
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
			});

		</script>
		
	</body>
</html>

