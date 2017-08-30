<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
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
		<script type="text/javascript" src="static/js/common/commonutils.js"></script>
		<!--提示框-->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<!-- <script type="text/javascript" src="static/js/angular.min.js"></script> -->
<script type="text/javascript">
	
</script>
	</head>
<body>
	<div class="row-fluid">
	<div class="span12">
		<form action="goods/saveStore.do" id="form"  method="post"> <!-- enctype="multipart/form-data" -->
		<input id="goodsId" type="hidden"  name="goodsId" value="${goodsId}"/>
		<input id="storeStr" type="hidden" name="storeStr">
		<c:choose>
			<c:when test="${not empty products}">
			<table id="table_bug_report" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						
						<th>序号</th>
						<c:forEach items="${products[0].goodsAttrNames}" var="attr">
							<td>${attr}</td>
						</c:forEach>
						<th>库存</th>
					</tr>
				</thead>
										
					<c:forEach items="${products}" var="p" varStatus="i">
					<tr>
						<td>${i.index+1}</td>
						<c:forEach items="${p.goodsAttrValues}" var="v">
							<td>${v}</td>
						</c:forEach>
						<td><input data-id="${p.productId}" name="productStore" min="0" max="10000" onkeyup="validateUtils.replaceNotNumEmpty(this);" type="number" value="${p.productStore}"/></td>
						
					</tr>
					</c:forEach>
					
					 <c:if test="${not empty goodsAttrs}">
									 <input id="attrIds" name="attrIds" type="hidden" >
							<c:forEach items="${goodsAttrs}" var="p" varStatus="i">
									<tr hidden="">
									<td name="vals" data-id="${p.goodsAttrId}" hidden="">${p.attrValue}</td>
									
									<!-- <td>
									 <input id="files" name="files" type="file">
									</td> -->
									</tr>
							</c:forEach>
					</c:if> 
			</table>
			</c:when>
			<c:otherwise>
				<div style="height:200px;"></div>
				<div align="center" style="height:100px;font-size:25px">
						请先编辑属性！
				</div>
			</c:otherwise>
		</c:choose>
		<div align="center" style="height:40px;">
           			 <a class="btn btn-mini btn-primary" id="save">保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
           			 <a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
		</div>
		</form>
	</div><!--/span-->
</div>
		<!--回到顶部  -->
		<a href="#" id="btn-scroll-up" class="btn btn-small btn-inverse"  title="回到顶部" >
			<i class="icon-double-angle-up icon-only"></i>
		</a>
	
	 	 <!-- 引入 -->
	  <script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
	  <script src="static/js/bootstrap.min.js"></script>
	  <script src="static/js/ace-elements.min.js"></script>
	  <script src="static/js/ace.min.js"></script>
	  <script type="text/javascript" src="static/js/chosen.jquery.min.js"></script>
		<script type="text/javascript" src="static/js/bootbox.min.js"></script><!-- 确认窗口 -->
		 <!-- base64 -->
	  <script src="static/js/base64.js"></script>
	  <script src="static/js/base64Compress.js"></script>
	  <link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
	  <script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
	  <!-- js工具类 -->
	 <script type="text/javascript" src="static/js/common/commonutils.js"></script>
	
	<script type="text/javascript">
		$(top.hangge());
	 	function selectImg($this){
			$this.style.display = "none";
			$($this).after('<input style="width: 75px;" type="file" onchange="selectImg(this)" name="files">');
			var files = $this.files;
				  for(var i=0;i<files.length;i++){
					  var FR= new FileReader();
					    FR.onload = function(e) {
					      //EL("img").src       = e.target.result;
					     // console.log(e.target.result);
					     //压缩base64 
					      var result = base64Shorter(e.target.result);
					     for(var i=1;i<="${fn:length(goodsAttrs)}";i++){
					      $('#images"'+i+'"').append('<img class="imgs" style="max-height:100px;max-width:100px;margin-left: 5px;margin-top: 5px" id="img" src="'+result+'">');
					     }
					    };
					  FR.readAsDataURL( files[i] );
				  }
		} 
		
		$("#save").click(function(){
			//获取商品属性库存
			var inputs = $("input[name=productStore]");
			var stores = [];
			$.each(inputs,function(index,val){
				stores.push({
					productId:$(val).data("id"),
					productStore:$(val).val()
				});
			});
			// console.log(JSON.stringify(stores));
			$("#storeStr").val(JSON.stringify(stores));
			
			//获取商品的属性 是有颜色
			var vals = $("td[name=vals]");
			var attrIds = [];
			$.each(vals,function(index,val){
				attrIds.push({
					ids:$(val).data("id")
				});
			})
			$("#attrIds").val(JSON.stringify(attrIds));
			$("#form")[0].submit();
	 	})
		 
	</script>
</body>
</html>