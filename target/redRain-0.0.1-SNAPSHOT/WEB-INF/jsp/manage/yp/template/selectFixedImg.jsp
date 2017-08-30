<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.aoshi.util.PropertyUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	<%@ include file="../../../system/admin/top.jsp"%>
	<!-- ztree -->
	</head> 
<body>
	<div id="page-content" class="clearfix">

		<div class="row-fluid">
			<!-- PAGE CONTENT BEGINS HERE -->
			<form class="form-horizontal" id="form" action="yp/admin/template/saveFixedImg.do" method="post" enctype="multipart/form-data">
				<input type="hidden" name="templateFixedId" value="${templateFixed.templateFixedId}"/>				
				<div class="control-group" >
					 <label class="control-label">图片</label>
					<div class="controls">
						
						<c:choose>
							<c:when test="${templateFixed.backgroundImage != null}">
								<input id="inp1" style="display: none" type='file' name="file">
								<span class="help-inline" style="text-align: center;">
									<a href="javascript:return false" onclick="addImg('inp1')">选择图片</a>
									<a href="javascript:return false" onclick="delFixedImg(${templateFixed.templateFixedId})">删除</a>
								</span>
								<div style="float: left;">
									<img id="image1" alt="" src="<%=httpUrl%>${templateFixed.backgroundImage}" style="max-width:350px;"/>
								</div>
							</c:when>
							<c:otherwise>
								<input id="inp1" type='file' name="file">
								<div style="float: left;">
									<img id="image1" alt="" src="" style="max-width:350px;"/>
								</div>
							</c:otherwise>
						</c:choose>
							
							<!-- <input id="goodsImg" type="hidden" name="goodsImg"> -->
					</div>
					<c:if test="${area != null}">
						<div class="control-group">
							<!-- <label class="control-label">标签名称</label> -->
							<div class="controls">
								<span>(建议尺寸:${area})</span>
							</div>
						</div>
					</c:if>
				</div>
				
				<div class="control-group">
					<label class="control-label">链接</label>
					<div class="controls">
						<input type='text' value="${templateFixed.url }" name="url">
					</div>
				</div>
				<div class="form-actions">
					
					<button class="btn btn-info" style="float: right;margin-right: 50%" type="button" id="save">
						<i class="icon-ok"></i> 保存
					</button>
					<!-- <button class="btn" type="reset">
						<i class="icon-undo"></i> Reset
					</button> -->
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
		<script src="static/js/base64Compress.js"></script>
		
  		<!-- 日期框 -->
  		<!-- 颜色 -->
		<script type="text/javascript">
		$(top.hangge());
		function EL(id) { return document.getElementById(id); } // Get el by ID helper function
		function readFile1() {
			  if (this.files && this.files[0]) {
			    var FR= new FileReader();
			    FR.onload = function(e) {
			      //EL("img").src       = e.target.result;
			     // console.log(e.target.result);
			     //压缩base64
			      var result = base64Shorter(e.target.result);
			     //var result = e.target.result;
			      $("#image1").prop("src",result);
			      //console.log(result);
			      
			    };       
			    FR.readAsDataURL( this.files[0] );
			  }
		}

		//EL("inp").addEventListener("change", readFile, false);
		EL("inp1").addEventListener("change", readFile1, false);
		function addImg(inp){
			$("#"+inp).click();
		};
		
		function delFixedImg(templateFixedId){
			if(confirm("确认删除图片吗?")){
				$.ajax({
						type: "POST",
						url: '<%=basePath%>yp/admin/template/delFixedImg.do',
				    	data: {templateFixedId:templateFixedId},
						dataType:'json',
						//beforeSend: validateData,
						cache: false,
						success: function(data){
							if(data){
								history.go(0);
							}
						}
					});
			  }
		}
		
		
		var hasClick = false;
		$(document).ready(function(){
			
			$("#save").on("click",function(){
				if(hasClick){
					return ;
				}
				
				hasClick = true;
				$("#form").submit();
			})
		})
		</script>
	</body>
</html>