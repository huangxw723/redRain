<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.aoshi.util.PropertyUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String httpUrl = PropertyUtils.getHTTP_URL();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">
<!-- jsp文件头和头部 -->
<%@ include file="../../../system/admin/top.jsp"%>
</head>
<body>

	<div class="container-fluid" id="main-container">


		<div id="page-content" class="clearfix">

			<div class="row-fluid">

				<div class="row-fluid">

					<!-- 检索  -->
					<form action="yp/business/screenInfo/listAll.do" method="post"
						name="shopForm" id="shopForm">
						<input type="hidden" id="currentPage" value="${pd.currentPage}" />
						<input type="hidden" id="showCount" value="${pd.showCount}" />
						<table>

			
							<tr>
								终端号:<span>
								<input name="sn" id="sn" type="text" value="${pd.sn}"style="width: 21%;"  />								</td>
								</span>
							           状态:
								<span><select name="status" id="status"
									style="width: 180px;">
										<option value="">全部</option>
											<option value="2" <c:if test="${pd.status =='2'}">selected="selected"</c:if>>审核不通过</option>
											<option value="1" <c:if test="${pd.status =='1' }">selected="selected"</c:if>>审核通过</option>
											<option value="0" <c:if test="${pd.status =='0' }">selected="selected"</c:if>>待审核</option>
								</select></span>
								<span class="input-icon"> 
									<input autocomplete="off" id="nav-search-input" type="text" id="url" name="url" value="${pd.url}" placeholder="域名" style="width:350px;"/>
										<i id="nav-search-icon" class="icon-search"></i>
										<button class="btn btn-mini btn-light" onclick="searchInfo();"
											title="检索">
											<i id="nav-search-icon" class="icon-search"></i>
										</button>
								</span>
							</tr>


						</table>
					</form>
					<!-- 检索  -->

                    <p>总安装屏数：${screenNum}个</p>
					<table id="table_report"
						class="table table-striped table-bordered table-hover">

						<thead>
							<tr>

								<th class="center">序号</th>
								<th class="center">终端编号</th>
								<th class="center">省份</th>
								<th class="center">市区</th>
								<th class="center">行政区</th>
								<th class="center">详细地址</th>
								<th class="center">域名</th>
								<th class="center">创建时间</th>
								<th class="center">状态</th>
								<th class="center">操作</th>

							</tr>
						</thead>

						<tbody>

							<!-- 开始循环 -->


							<c:choose>
								<c:when test="${not empty list}">
									<c:if test="${QX.cha == 1 }">
										<c:forEach items="${list}" var="var" varStatus="vs">
											<tr>

												<td
													style="text-align: center; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
													title="${vs}">${vs.index+1}</td>
												<td
													style="text-align: center; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
													title="${var.sn}">${var.sn}</td>
												<td class='center' style="width: 80px;">${var.province_name}</td>
												<td
													style="text-align: center; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
													title="${var.cityName}">${var.city_name}</td>
												<td
													style="text-align: center; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
													title="${var.areaName}">${var.area_name}</td>
												<td
													style="text-align: center;  width: 190px"
													title="${var.address}">${var.address}</td>
											  
												<td
													style="text-align: center;"
													title="">${var.url}</td>
												<td
													style="text-align: center; width: 150px"
													title="">${var.createTime}</td>
												<c:if test="${var.status==1}">
												<td class='center' style="width: 70px;">审核通过</td>
												</c:if>
												<c:if test="${var.status==2}">
												<td class='center' style="width: 70px;">审核不通过</td>
												</c:if>
												<c:if test="${var.status==0}">
												<td class='center' style="width: 70px;"><span style="font-size: 10px;color: red;">待审核</span></td>
												</c:if>
												<td style="width: 32px;" class="center">
													<div class="inline position-relative">
														<c:if test="${QX.edit == 1}">
															<a href="javascript:void(0);"
																onclick="edit(${var.screenInfoId},2)">编辑</a>
														</c:if>
													</div>
												</td>
											</tr>

										</c:forEach>
									</c:if>
									<c:if test="${QX.cha == 0 }">
										<tr>
											<td colspan="100" class="center">您无权查看</td>
										</tr>
									</c:if>
								</c:when>
								<c:otherwise>
									<tr class="main_info">
										<td colspan="100" class="center">没有相关数据</td>
									</tr>
								</c:otherwise>
							</c:choose>


						</tbody>
					</table>
					<div class="page-header position-relative">
						<table style="width: 100%;">
							<tr>

								<td style="vertical-align: top;">
								<a
									class="btn btn-small btn-success" onclick="add('1');">新增</a>
									<div class="pagination"
										style="float: right; padding-top: 0px; margin-top: 0px;">${page.pageStr}</div>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<!-- PAGE CONTENT ENDS HERE -->
			</div>
			<!--/row-->

		</div>
		<!--/#page-content-->
	</div>
	<!--/.fluid-container#main-container-->

	<!-- 返回顶部  -->
	<a href="#" id="btn-scroll-up" class="btn btn-small btn-inverse"> <i
		class="icon-double-angle-up icon-only"></i>
	</a>

	<!-- 引入 -->
	<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/ace-elements.min.js"></script>
	<script src="static/js/ace.min.js"></script>

	<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script>
	<!-- 下拉框 -->
	<script type="text/javascript"
		src="static/js/bootstrap-datepicker.min.js"></script>
	<!-- 日期框 -->
	<script type="text/javascript" src="static/js/bootbox.min.js"></script>
	<!-- 确认窗口 -->
	<!-- 确认窗口 -->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<link rel="stylesheet" type="text/css"
		href="plugins/fancyBox/jquery.fancybox.css">
	<script type="text/javascript"
		src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
	<!-- 引入 -->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<!--提示框-->
	<script type="text/javascript">
		
		$(top.hangge());
		
		//检索
		function searchInfo(){
			top.jzts();
			$("#shopForm").submit();
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
	    
     function accountView(id){
			
			var form = $("#usersForm");
			form.find('#currentPage').attr('name','currentPage');
			form.find('#showCount').attr('name','showCount');
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="资料";
			 diag.URL = '<%=basePath%>yp/business/ypShop/getAccountList.do?shopId='+id;
			 diag.Width = 700;
			 diag.Height = 600;
			 diag.CancelEvent = function(){ //关闭事件
				diag.close();
				form.submit();
			 };
			 diag.show();
		}
       
		//新增
		function add(otype){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="新增终端信息";
			 diag.URL = '<%=basePath%>yp/business/screenInfo/page.do?action='+otype;
			 diag.Width = 810;
			 diag.Height = 450;
			 diag.CancelEvent = function () { //关闭事件
		            if (diag.innerFrame.contentWindow.document.getElementById('zhongxin').style.display == 'none') {
		                if ('${page.currentPage}' == '0') {
		                    top.jzts();
		                    setTimeout("self.location=self.location", 100);
		                } else {
		                	location.reload();
		                }
		            }
		            diag.close();
		        };
			 
			 diag.show();
		}
		//编辑
		function edit(id,otype){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="编辑终端信息";
			 diag.URL = '<%=basePath%>yp/business/screenInfo/page.do?action='+otype+'&screenInfoId='+id;
			 diag.Width = 7950;
			 diag.Height = 450;
			 diag.CancelEvent = function () { //关闭事件
		            if (diag.innerFrame.contentWindow.document.getElementById('zhongxin').style.display == 'none') {
		                if ('${page.currentPage}' == '0') {
		                    top.jzts();
		                    setTimeout("self.location=self.location", 100);
		                } else {
		                	location.reload();
		                }
		            }
		            diag.close();
		        };
			 
			 diag.show();
		}
		
		</script>

	<script type="text/javascript">
		
		$(function() {
			
			//下拉框
			$(".chzn-select").chosen(); 
			$(".chzn-select-deselect").chosen({allow_single_deselect:true}); 
			$('.date-picker').datepicker();
			//复选框
			$('table th input:checkbox').on('click' , function(){
				var that = this;
				$(this).closest('table').find('tr > td:first-child input:checkbox')
				.each(function(){
					this.checked = that.checked;
					$(this).closest('tr').toggleClass('selected');
				});
					
			});
			
			
			$('#startTime').datepicker({  
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
			});
			
		});
		
		</script>

</body>
</html>

