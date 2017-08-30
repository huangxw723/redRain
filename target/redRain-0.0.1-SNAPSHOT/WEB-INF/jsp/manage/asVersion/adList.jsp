﻿<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.aoshi.util.PropertyUtils" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	  String httpUrl = PropertyUtils.getHTTP_URL();
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
		<base href="<%=basePath%>"><!-- jsp文件头和头部 -->
	<%@ include file="../../system/admin/top.jsp"%>
</head> 
<body>
		
<div class="container-fluid" id="main-container">


<div id="page-content" class="clearfix">
						
  <div class="row-fluid">


	<div class="row-fluid">
	
			<!-- 检索  -->
			<form action="version/listAll.do" method="post" name="versionForm" id="Form">
			<input type="hidden" name="action" id="action" value="${action}"/>
			<input type="hidden" id="currentPage" value="${pd.currentPage}"/>
			<input type="hidden" id="showCount" value="${pd.showCount}"/>
				<table>
					<tr>
					<td style="vertical-align:top;">
						<span>
							平台类型:
							<select name="platform">
								<option value="" <c:if test="${asversion =='' }">selected="selected" </c:if>>全部</option>
								<option value="1" <c:if test="${asversion == '1' }">selected="selected" </c:if>>ios</option>
								<option value="2" <c:if test="${asversion == '2' }">selected="selected" </c:if>>android</option>
							</select>
						</span>
						<span class="input-icon"> 
							<input autocomplete="off" id="nav-search-input" type="text" name="keyWord" value="${pd.keyWord}" placeholder="这里输入版本号" />
						</span>
					</td>
					<td style="vertical-align:top;">
						&nbsp;
                                <button class="btn btn-mini btn-light" onclick="search();" title="检索">
                                    <i id="nav-search-icon" class="icon-search"></i>
                                </button>
                            </td>
						<td style="vertical-align: top;">
							&nbsp;
							<a class="btn btn-small btn-success" onclick="add();">新增</a>
						</td>
					</tr>

				</table>		
			</form>
			<!-- 检索  -->
		
		
			<table id="table_report" class="table table-striped table-bordered table-hover">
				
				<thead>
					<tr>
					
						<th style="text-align:center;">版本</th>
						<th style="text-align:center;">平台类型</th>
						<th class="center">是否显示</th>
						<th style="text-align:center;">备注</th>
						<th style="text-align:center;">下载地址url</th>
			            <th style="text-align:center;">操作</th>
					</tr>
				</thead>
										
				<tbody>
					
				<!-- 开始循环 -->	
				<c:choose>
					<c:when test="${not empty list}">
						<c:forEach items="${list}" var="var" varStatus="vs">
			                <tr>
								<td class='center' style="text-align:center;">${var.versionCode}</td> 
								<c:if test="${var.platform == '1' }">
								<td class='center' style="text-align:center;">ios</td>  
								</c:if>
								<c:if test="${var.platform == '2' }">
								<td class='center' style="text-align:center;">android</td>  
								</c:if>
								<c:if test="${var.isShow==1}">
								   <td class='center' style="text-align:center;">显示</td>
								</c:if>
								<c:if test="${var.isShow==0}">
								   <td class='center' style="text-align:center;">不显示</td>
								</c:if>
								<td class='center' style="text-align:center;">${var.remark}</td>
								<td class='center' style="text-align:center;">${var.downloadUrl}</td>
								<td class='center' style="text-align:center;">
									<a href="javascript:void(0);" onclick="query(${var.versionId})">详情</a>
									<a href="javascript:void(0);" onclick="edit(${var.versionId})">编辑</a>
									<a href="javascript:void(0);" onclick="idelete(${var.versionId})">删除</a>
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
		</form>
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
		<!-- 确认窗口 -->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
		<script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
		<!-- 引入 -->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script><!--提示框-->
		<script type="text/javascript">
		
		$(top.hangge());
		//检索
		function search(){
			top.jzts();
			var form = $("#Form");
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			$("#Form").submit();
		}
		//新增
		function add(){
			top.jzts();
			var diag = new top.Dialog();
			diag.Drag=true;
			diag.Title ="新增";
			diag.URL = '<%=basePath%>version/page.do?action=1';
			diag.Width = 550;
			diag.Height = 570;
			diag.CancelEvent = function(){ //关闭事件
				diag.close();
				search();
			};
			diag.show();
		}
		//修改
		function edit(id){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="资料";
			 diag.URL = '<%=basePath%>version/page.do?action=2&versionId='+id;
			 diag.Width = 550;
			 diag.Height = 570;
			 diag.CancelEvent = function(){ //关闭事件
				diag.close();
				search();
			 };
			 diag.show();
		}
		
		function query(id){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="资料";
			 diag.URL = '<%=basePath%>version/page.do?action=4&versionId='+id;
			 diag.Width = 530;
			 diag.Height = 530;
			 diag.CancelEvent = function(){ //关闭事件
				diag.close();
				search();
			 };
			 diag.show();
		}
		
		
		//删除
		function idelete(id){
			bootbox.confirm("确定要删除吗?",function(result){
				if(result) {
					$.get("version/delete",{versionId:id},function(data){
						if(data.status != 0){
							alert(data.msg);
							setTimeout("self.location=self.location",100);
							return;
						}
						search();
					},'json');
				}
			});
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

			$(".fancybox").fancybox({
	            helpers: {
	                title: {
	                    type: 'inside'
	                }
	            }
	        });
			
		});
		
		
		
		</script>
		
	</body>
</html>

