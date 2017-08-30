<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	<%@ include file="../../system/admin/top.jsp"%>
	<!-- ztree -->
	<link type="text/css" rel="stylesheet" href="plugins/zTree/3.5/demo.css" />
  	<link type="text/css" rel="stylesheet" href="plugins/zTree/3.5/zTreeStyle.css" />
	</head> 
<body>
	<div id="page-content" class="clearfix">

		<div class="row-fluid">
			<!-- PAGE CONTENT BEGINS HERE -->
			<form class="form-horizontal" id="form" action="coupon/addCoupon.do" method="post" enctype="multipart/form-data">

				<div class="control-group">
					<label class="control-label">优惠券名称</label>
					<div class="controls">
						<input type="text" id="name" name="name" placeholder="优惠券名称" required />
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label">优惠券类型</label>
					<div class="controls">
						<select name="prizeTypeId">
							<c:forEach items="${prizeTypes}" var="t">
								<option value="${t.prizeTypeId}">${t.prizeTypeName}</option>
							</c:forEach>
							
						</select>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label">优惠券颜色</label>
					<div class="controls">
						<input name="color" class="jscolor" value="ffffff" />
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label">标签</label>
					<div class="controls">
						<input id="tagIds" type="hidden" name="tagIds" value="">
						<c:forEach items="${tags}" var="tag">
							<label>
								<input class="tagId" type="checkbox" value="${tag.tagId }"/>
								<span class="lbl"> ${tag.name}</span>
							</label>
						</c:forEach>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">logo</label>
					<div class="controls">
						<input id="" type='file' name="file">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">开始时间</label>
					<div class="controls">
						<input id="startTime" type='text' value="${coupon.startTime }" name="startTime" class="date-picker" data-date-format="yyyy-mm-dd" readonly="readonly">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">结束时间</label>
					<div class="controls">
						<input id="endTime" type='text' value="${coupon.endTime }" name="endTime" class="date-picker" data-date-format="yyyy-mm-dd" readonly="readonly">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">面额</label>
					<div class="controls">
						<input id="price" type='number' value="${coupon.price }" name="price">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">描述</label>
					<div class="controls">
						<input id="" type='text' value="${coupon.descp}" name="descp">
					</div>
				</div>
				<div class="form-actions">
					<button class="btn btn-info" type="button" id="save">
						<i class="icon-ok"></i> Submit
					</button>
					&nbsp; &nbsp; &nbsp;
					<button class="btn" type="reset">
						<i class="icon-undo"></i> Reset
					</button>
				</div>
			</form>

		</div>
		<!-- PAGE CONTENT ENDS HERE -->
						 </div><!--/row-->
	
		<!-- basic scripts -->
		<script src="static/1.9.1/jquery.min.js"></script>
		<script type="text/javascript">
		window.jQuery || document.write("<script src='js/jquery-1.9.1.min.js'>\x3C/script>");
		</script>
		
		<script src="static/js/bootstrap.min.js"></script>
		
		
		
		<!-- ace scripts -->
		<script src="static/js/ace-elements.min.js"></script>
		<script src="static/js/ace.min.js"></script>
		<!-- inline scripts related to this page -->
		<!-- ztree -->
		<script type="text/javascript" src="plugins/zTree/3.5/jquery.ztree.core.min.js"></script>
		<script type="text/javascript" src="plugins/zTree/3.5/jquery.ztree.excheck.min.js"></script>
		<script src="static/js/base64Compress.js"></script>
		
		<!-- 编辑框-->
		<script type="text/javascript" charset="utf-8">window.UEDITOR_HOME_URL = "<%=path%>/plugins/ueditor/";</script>
		<script type="text/javascript" charset="utf-8" src="plugins/ueditor/ueditor.config.js"></script>
		<script type="text/javascript" charset="utf-8" src="plugins/ueditor/ueditor.all.js"></script>
		<!-- 编辑框-->
		<script type="text/javascript" src="static/js/bootstrap-datepicker.min.js"></script>
  		<!-- 日期框 -->
  		<!-- 颜色 -->
  		<script src="static/js/jscolor.min.js"></script>
		<script type="text/javascript">
		$(document).ready(function(){
			$(top.hangge());
			
			$('.date-picker').datepicker()
			
			$("#save").on("click",function(){
				var name = $("#name").val();
				var startTime = $("#startTime").val();
				var endTime = $("#endTime").val();
				
				if(undefined == startTime || startTime.trim()==""){
					alert("开始时间不能为空");
					return ;
				}
				if(undefined == endTime || endTime.trim()==""){
					alert("结束时间不能为空");
					return ;
				}
				var now = new Date();
				if(Date.parse(endTime)<Date.parse(now)){
					alert("不能添加过期的优惠券");
					return ;
				}
				if(undefined == name || name.trim()==""){
					alert("名称不能为空");
					return ;
				}
				var inps = $(".tagId:checked");
				if(inps.length == 0){
					alert("请选择标签");
					return ;
				}
				
				var tagIds = "";
				$.each(inps,function(index,val){
					tagIds = tagIds+$(val).val()+",";
				})
				tagIds = tagIds.substring(0,tagIds.length-1);
				$("#tagIds").val(tagIds);
				
				$("#form").submit();
			})
			
		})
			
		</script>
	</body>
</html>