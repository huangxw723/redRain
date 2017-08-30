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
			<form action="prizeconfig/listHistory.do" method="post" name="usersForm" id="usersForm">
			<input type="hidden" id="currentPage" value="${pd.currentPage}"/>
			<input type="hidden" id="showCount" value="${pd.showCount}"/>
			<input type="hidden" id="activityType" name="activityType" value="${activityType}"/>
				<input type="hidden" id="isJackpot" name="isJackpot" value="${pd.isJackpot}"/>
				<table>
					<tr>
						<c:if test="${shopId == 'null'||shopId == null}">
								 <td style="vertical-align:top;"> 
					                  <select class="chzn-select" name="provinceId" id="provinceId" data-placeholder="请选择省份" style="vertical-align:top;width: 120px;">
					                    <option value=""></option>
					                    <option value="">全部</option>
					                    <c:forEach items="${provinceList}" var="province">
					                      <option value="${province.provinceId }" <c:if test="${pd.provinceId==province.provinceId}">selected</c:if>>${province.provinceName }</option>
					                    </c:forEach>
					                  </select>
	                			</td>
	                			<td style="vertical-align:top;"> 
	                  				<select class="chzn-select" name="cityId" id="cityId" data-placeholder="请选择城市" style="vertical-align:top;width: 120px;">
	                 				 </select>
	                			</td>
	                			<td style="vertical-align:top;"> 
	                				 <select class="chzn-select" name="areaId" id="areaId" data-placeholder="请选择县区" style="vertical-align:top;width: 140px;">
	                 				 </select>
	                			</td>
							</c:if>
					
					
							<td>
								<span>
									礼品类型:
									<select name="categoryId" id="categoryId" style="vertical-align:top;width: 90px">
										<option value="">全部</option>
										<c:forEach items="${prizeTypeList}" var="var" varStatus="index">
												<option value="${var.id}" <c:if test="${var.id eq pd.categoryId}">selected</c:if>>${var.name}</option>
										</c:forEach>
									</select>
								</span>
								<span>
									状态:
									<select name="status" id="status" style="vertical-align:top;width: 70px">
										<option value="">全部</option>
										<option value="3" <c:if test="${pd.status eq 3}">selected="selected"</c:if>>派完</option>
										<option value="4" <c:if test="${pd.status eq 4}">selected="selected"</c:if>>过期</option>
										<option value="5" <c:if test="${pd.status eq 5}">selected="selected"</c:if>>删除</option>
									</select>
								</span>
								<span class="input-icon"> 
									<input autocomplete="off" id="nav-search-input" type="text" name="keyWord" value="${pd.keyWord}" placeholder="礼品编号/礼品名称/商户名称/商场" style="width:220px;"/>
										<i   class="icon-search"></i>
								</span>
							</td>
							<td style="vertical-align: top;">
										&nbsp;
										<button class="btn btn-mini btn-light" onclick="search();"
											title="查询">
											<i id="nav-search-icon" class="icon-search"></i>
										</button>
								</td>
							<td style="vertical-align: top;">
						&nbsp;
						<a class="btn btn-small btn-success" onclick="toExcel();">导出excel</a>

					</td>
						</tr>		
				</table>		
			</form>
			<!-- 检索  -->
		
			<table id="table_report" class="table table-striped table-bordered table-hover">
				
				<thead>
					<tr>
						<th style="text-align:center;">礼品编号</th>
						<th style="text-align:center;">礼品名称</th>
						<th style="text-align:center;">礼品类型</th>
						<th style="text-align:center;">商户名称(商场)</th>
						<th style="text-align:center;">实际兑换</th>
						<th style="text-align:center;">礼品数量(余/总)</th>
						<th style="text-align:center;">开始时间</th>
						<th style="text-align:center;">结束时间</th>
						<th style="text-align:center;">状态</th>
						<th style="text-align:center;">操作</th>
					</tr>
				</thead>
										
				<tbody>
					
				<!-- 开始循环 -->	
				<c:choose>
					<c:when test="${not empty list}">
						<c:forEach items="${list}" var="var" varStatus="vs">
									
							<tr>
								<td class='center' style="text-align:center;">${var.sn}</td>
								<td class='center' style="text-align:center;">${var.name}</td>
								<td class='center' style="text-align:center;">${var.prizeTypeName}</td>
								<td class='center' style="text-align:center;">
									${var.shopName}&nbsp;
									<c:if test="${var.storeName !=null}">(${var.storeName})</c:if>
								</td>
								<td class='center' style="text-align:center;">
									<c:forEach items="${exchangeCountList}" var="var1" >
										<c:if test="${var1.prizeConfigId == var.prizeConfigId}">${var1.exchangeCount}</c:if>
									</c:forEach>
								</td>
								<td class='center' style="text-align:center;">${var.surplusCounts}/${var.counts}</td>
								<td class='center' style="text-align:center;">${var.startTime}</td>
								<td class='center' style="text-align:center;">${var.endTime}</td>
								<td class='center' style="text-align:center;">
									<c:choose>
										<c:when test="${var.status == 5 }">
											<span>删除</span>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${var.status == 3 or  var.surplusCounts==0}">
													<span>派完</span>
												</c:when>
												<c:otherwise>
													<span>过期</span>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</td>
								<td class='center'  style="text-align:center;">
										<a href="javascript:void(0);" onclick="query(${var.prizeConfigId})">详情</a>
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
		<script type="text/javascript">
		
		$(top.hangge());
		
		$(function() {
			if($("#provinceId").val()) {
				var provinceId = $("#provinceId").val();
			    var citySelect = $("#cityId");
			    citySelect.empty().append("<option value=''></option>").append("<option value=''>全部</option>");
			    var cityList = '${cityList}';
			    var cityId = '${pd.cityId}';
			    $.each(JSON.parse(cityList), function(i, list){
			    	var parentId = list.provinceId;
					if(parentId == provinceId) {
						var value = list.cityId;
						var option = list.cityName;
						var label = $("<option value='"+value+"'>"+option+"</option>");
						if(cityId == list.cityId) {
							label.attr("selected", "selected");
						}
						citySelect.append(label);
					}
			 	});
			}
			
			if($("#cityId").val()) {
				var cityId = $("#cityId").val();
			    var areaSelect = $("#areaId");
			    areaSelect.empty().append("<option value=''></option>").append("<option value=''>全部</option>");
			    var areaList = '${areaList}';
			    var areaId = '${pd.areaId}';
			    $.each(JSON.parse(areaList), function(i, list){
			    	var parentId = list.cityId;
					if(parentId == cityId) {
						var value = list.areaId;
						var option = list.areaName;
						var label = $("<option value='"+value+"'>"+option+"</option>");
						if(areaId == list.areaId) {
							label.attr("selected", "selected");
						}
						areaSelect.append(label);
					}
			 	});
			}
			
			//联动select事件
			$("#provinceId").change(function(){
			    var provinceId = $("#provinceId").val();
			    var citySelect = $("#cityId");
			    citySelect.empty().append("<option value=''></option>").append("<option value=''>全部</option>");
			    var areaSelect = $("#areaId");
			    areaSelect.empty().append("<option value=''></option>").append("<option value=''>全部</option>");
			    var cityList = '${cityList}';
			    $.each(JSON.parse(cityList), function(i, list){
			    	var parentId = list.provinceId;
					if(parentId == provinceId) {
						var value = list.cityId;
						var option = list.cityName;
						var label = $("<option value='"+value+"'>"+option+"</option>");
						citySelect.append(label);
					}
			 	});
			    citySelect.trigger("liszt:updated");
			    areaSelect.trigger("liszt:updated");
			});
			
			//联动select事件
			$("#cityId").change(function(){
			    var cityId = $("#cityId").val();
			    var areaSelect = $("#areaId");
			    areaSelect.empty().append("<option value=''></option>").append("<option value=''>全部</option>");
			    var areaList = '${areaList}';
			    $.each(JSON.parse(areaList), function(i, list){
			    	var parentId = list.cityId;
					if(parentId == cityId) {
						var value = list.areaId;
						var option = list.areaName;
						var label = $("<option value='"+value+"'>"+option+"</option>");
						areaSelect.append(label);
					}
			 	});
			    areaSelect.trigger("liszt:updated");
			});
		});
		
		//检索
		function search(){
			top.jzts();
			var form = $("#usersForm");
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			$("#usersForm").submit();
		}

		//导出excel
		function toExcel(){

			var provinceId = '${pd.provinceId}';
			var cityId = '${pd.cityId}';
			var areaId = '${pd.areaId}';
			var categoryId = '${pd.categoryId}';
			var status = '${pd.status}';
			var isJackpot = '${pd.isJackpot}';
			var keyWord = '${pd.keyWord}';
			var currentPage = '${pd.currentPage}';
			var showCount = '${pd.showCount}';

			window.location.href='<%=basePath%>prizeconfig/bigWheelExcelHistory.do?provinceId='+provinceId+'&cityId='+cityId+'&areaId='+areaId+'&categoryId='+categoryId+'&status='+status+'&isJackpot='+isJackpot+'&keyWord='+keyWord+'&showCount='+showCount+'&currentPage='+currentPage;

		}
		
		function query(id){
			
			var form = $("#usersForm");
			var parems = 'action=4'+ '&prizeConfigId='+id+'&activityType='+"${activityType}"+"&isJackpot="+"${pd.isJackpot}" ;
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="资料";
			 diag.URL = '<%=basePath%>prizeconfig/page.do?'+parems;
			 diag.Width = 1150;
			 diag.Height = 610;
			 diag.CancelEvent = function(){ //关闭事件
				diag.close();
				//form.submit();
			 };
			 diag.show();
		}
		
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

