<%@ page import="com.aoshi.util.PropertyUtils" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	<%@ include file="../../system/admin/top.jsp"%>
	<meta charset="utf-8" />
  		<script type="text/javascript">
  		//选择框
  		$(function() {
			$(".chzn-select").chosen(); 
			$(".chzn-select-deselect").chosen({allow_single_deselect:true}); 
  		});
  			
  		//更改图片
  	    function changeImg(){
  	        $("#fileInput").trigger("click");
  	    }
  		//检查商品名称是否重名
		var isExist = false
		function checkExist(){
		var goodsName = $("#goodsName").val();
		var oldName = '${goods.goodsName}';
			if(goodsName != "" || $.trim(goodsName)!=""){
				if(goodsName != oldName){
				$.ajax({
					url:"<%=basePath%>goods/checkExist",
					type:"GET",
					dataType:"json",
					data:{goodsName:goodsName},
					success:function(data){
						if(data.isExist){
							isExist = true;
							$("#goodsName").tips({
								side:3,
					            msg:'该商品名称已经存在，请重新输入!',
					            bg:'#AE81FF',
					            time:2
					        });
							$("#goodsName").focus();
						}else{
							isExist = false;
						}
					}
				})
			}
		  }
		}	
		//检查商品货号sn是否重复
		var isExistSn = false
		function checkExistSn(){
		var goodsSn = $("#goodsSn").val();
		var oldSn = '${goods.goodsSn}';
			if(goodsSn != "" || $.trim(goodsSn)!=""){
				if(goodsSn != oldSn){
					$.ajax({
						url:"<%=basePath%>goods/checkExistSn",
						type:"GET",
						dataType:"json",
						data:{goodsSn:goodsSn},
						success:function(data){
							if(data.isExist){
								isExist = true;
								$("#goodsSn").tips({
									side:3,
						            msg:'该商品货号已经存在，请更换！',
						            bg:'#AE81FF',
						            time:2
						        });
								$("#goodsSn").focus();
							}else{
								isExist = false;
							}
						}
					})
				}	
			}
		}


		//获取积分兑换比值
		//var salesPrice = '${goods.salesPrice }';
		function  setSalesPrice() {
			var mp = $("#marketPrice").val();
			if (mp != "" || $.trim(mp) != "") {
				$.ajax({
					url: "<%=basePath%>point/getMoneyExchange",
					type: "GET",
					success: function (data) {
						var temp = Math.round((data * mp));
						//salesPrice =temp;
						$("#salesPrice").val(temp);
					},
					error:function () {
						//设置默认的积分兑换比值
						var temp = 10 * mp;
						//salesPrice =temp;
						$("#salesPrice").val(temp);
					}
				})

			}
		}
  		
			//保存
			function save(){
				if($("#goodsName").val()=="" || $.trim($("#goodsName").val())==""){
					$("#goodsName").tips({
						side:3,
			            msg:'请输入商品名称',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#goodsName").focus();
					return false;
				}
				if($("#goodsName").val().length<2 || $("#goodsName").val().length>50){
					$("#goodsName").tips({
						side:3,
			            msg:'2~50个字',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#goodsName").focus();
					return false;
				}
				var goodsName = $("#goodsName").val();
				var pattern = new RegExp(/^\"|\"|\'|\'$/g);
				var b = pattern.test(goodsName);
				if(b){
					$("#goodsName").tips({
						side:3,
						msg:'名称中不能包含英语的双引号或单引号',
						bg:'#AE81FF',
						time:2
					});
					$("#goodsName").focus();
					return false;
				}
				var goodsName = $("#goodsName").val();
				var oldName = '${goods.goodsName}';
				if(goodsName != oldName){
					if(isExist){
						$("#goodsName").tips({
							side:3,
				            msg:'该商品名称已经存在，请重新输入!',
				            bg:'#AE81FF',
				            time:2
				        });
						$("#goodsName").focus();
						return false;
					}
				}
				if($("#goodsSn").val()=="" || $.trim($("#goodsSn").val())==""){
					$("#goodsSn").tips({
						side:3,
			            msg:'请输入商品货号',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#goodsSn").focus();
					return false;
				}
				if($("#goodsSn").val().length<2 || $("#goodsSn").val().length>20){
					$("#goodsSn").tips({
						side:3,
			            msg:'2~20个',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#goodsSn").focus();
					return false;
				}
				var goodsSn = $("#goodsSn").val();
				var oldSn = "${goods.goodsSn}";
				if(goodsSn != oldSn){
					if(isExistSn){
						$("#goodsSn").tips({
							side:3,
				            msg:'该商品货号已经存在，请更改!',
				            bg:'#AE81FF',
				            time:2
				        });
						$("#goodsSn").focus();
						return false;
					}
				}
				if($("#marketPrice").val()=="" || $.trim($("#marketPrice").val())==""){
					$("#marketPrice").tips({
						side:3,
			            msg:'请输入市场价',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#marketPrice").focus();
					return false;
				}
				
				
				if($("#marketPrice").val() > 99999.99  || $("#marketPrice").val() < 0.01  ){
					$("#marketPrice").tips({
						side:3,
			            msg:'市场价: 0.01~99999.99',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#marketPrice").focus();
					return false;
				}
				if($("#marketPrice").val() < 99999.99|| $("#marketPrice").val() > 0.01 ){
					var pattern = /^(([1-9]+)|([0-9]+\.?[0-9]{0,2}))$/;
					var b = pattern.test($("#marketPrice").val());
					if(!b){
						$("#marketPrice").tips({
							side:3,
				            msg:'市场价格式: 0.01~99999.99',
				            bg:'#AE81FF',
				            time:3
				        });
						$("#marketPrice").focus();
						return false;
					}
				}

				if($("#salesPrice").val()=="" || $.trim($("#salesPrice").val())==""){
					$("#salesPrice").tips({
						side:3,
			            msg:'请输入兑换积分',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#salesPrice").focus();
					return false;
				}
				if($("#salesPrice").val() > 1000000 || $("#salesPrice").val() < 1  ){
					$("#salesPrice").tips({
						side:3,
			            msg:'兑换积分: 1~1000000',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#salesPrice").focus();
					return false;
				}
				if($("#salesPrice").val() < 1000000|| $("#salesPrice").val() > 1 ){
					var temp = $("#salesPrice").val();
					var value = temp.split('.')[0];
					$('#salesPrice').val(value);
				}


				if($("#unitId").val()=="" || $.trim($("#unitId").val())==""){
					$("#unitId").tips({
						side:3,
			            msg:'请选择商品单位',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#unitId").focus();
					return false;
				}
				
				if($("#weight").val()=="" || $.trim($("#weight").val())==""){
					$("#weight").tips({
						side:3,
			            msg:'请输入商品重量',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#weight").focus();
					return false;
				}
				if($("#weight").val() > 99.99  || $("#weight").val() < 0.01  ){
					$("#weight").tips({
						side:3,
			            msg:'商品重量: 0.01~99.99',
			            bg:'#AE81FF',
			            time:2
			        });
					$("#weight").focus();
					return false;
				}
				
				if($("#weight").val() < 99.99|| $("#weight").val() > 0.01 ){
					var pattern = /^(([1-9]+)|([0-9]+\.?[0-9]{0,2}))$/;
					var b = pattern.test($("#weight").val());
					if(!b){
						$("#weight").tips({
							side:3,
				            msg:'商品重量格式: 0.01~99.99',
				            bg:'#AE81FF',
				            time:3
				        });
						$("#weight").focus();
						return false;
					}
				}
				
				var treeObj = $.fn.zTree.getZTreeObj("tree");
				var selectedNodes = treeObj.getCheckedNodes(true);
				
				if(selectedNodes.length==0){
					bootbox.alert("请选择商品分类");
					$("#tree").tips({
						side:3,
			            msg:'请选择商品分类',
			            bg:'#AE81FF',
			            time:5
			        });
					$("#weight").focus();
					return false;
				}
				
				//判断只能勾选最小分类
				var goodsTypeLevel = [];
				$.each(selectedNodes,function(index,val){
					goodsTypeLevel.push(val.level);
				});
				if($.inArray(2, goodsTypeLevel)==-1){
					bootbox.alert("请勾选最小分类");
					$("#tree").tips({
						side : 3,
						msg : '请勾选最小分类',
						bg : '#AE81FF',
						time : 5
					});
					$("#weight").focus();
					return false;
				}
				//把已选择的商品分类id 放到数组
				var goodsType = [];
				$.each(selectedNodes,function(index,val){
					goodsType.push({
						goodsTypeId:val.goodsTypeId
					});
					
				});
				//console.log(JSON.stringify(goodsType));
				//把商品分类id序列化成json格式
				$("#goodsTypeStr").val(JSON.stringify(goodsType));
				
				var fileType = document.getElementById("fileInput").value.substr(document.getElementById("fileInput").value.lastIndexOf(".")).toLowerCase();//获得文件后缀名
				var url = $("#url").val();
				var img = $("#imgHref").attr("src");
				if (!img || url) {
					if (fileType != '.gif' && fileType != '.png' && fileType != '.jpg'&& fileType != '.bmp'
							&& fileType != '.jpeg') {
						bootbox.alert("请上传图片格式的文件");
						$("#chImg").tips({
							side : 3,
							msg : '请上传图片格式的文件',
							bg : '#AE81FF',
							time : 5
						});
						$("#weight").focus();
						$("#fileInput").val('');
						document.getElementById("fileInput").files[0] = '请选择图片';
						return;
					}
				}
				
				var img = document.getElementById("fileInput").files;
				if(img[0] != undefined){
					var imgSize = img[0].size/1024;
					if(imgSize>2048){
						bootbox.alert("图片不能大于2M,请更换图片");
						$("#chImg").tips({
							side : 3,
							msg : '图片不能大于2M,请更换图片',
							bg : '#AE81FF',
							time : 6
						});
						$("#weight").focus();
						return false;
					}
				}
				//把商品描述序列化成字符串				
				var oEditor = CKEDITOR.instances.descp; 
				var content = oEditor.getData();
				if(validateUtils.isEmpty(content)){
					$("#descp").tips({
						side : 3,
						msg : '请填写商品描述',
						bg : '#AE81FF',
						time : 6
					});
					$("#descp").focus();
					return
				} else{
					$('#descp').val(content);
				}
				if($("#descp").val().length<2 || $("#descp").val().length>10000){
					$("#descp").tips({
						side:3,
			            msg:'2~10000个字',
			            bg:'#AE81FF',
			            time:5
			        });
					$("#descp").focus();
					return false;
				}
				$("#form").submit();
			}
			
			
	</script>
	</head> 
<body>
	<div id="page-content" class="clearfix">

		<div class="row-fluid">
			<!-- PAGE CONTENT BEGINS HERE -->
			<form class="form-horizontal" id="form" action="goods/editGoods.do" method="post">
				<input type="hidden" name="goodsId" value="${goods.goodsId }">
				<div class="control-group">
					<label class="control-label">商品名称</label>
					<div class="controls">
						<textarea style="width:210px;height:60px;"  id="goodsName"  name="goodsName" placeholder="2~50个字" required onblur="checkExist()" >${goods.goodsName }</textarea>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">商品货号</label>
					<div class="controls">
						<input type="text" value="${goods.goodsSn }" id="goodsSn" name="goodsSn" placeholder="2~20个" required onblur="checkExistSn()"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">市场价</label>
					<div class="controls">
						<input type="number" value="${goods.marketPrice }"  min="0.01" max="99999.99" id="marketPrice" name="marketPrice" onchange="setSalesPrice();" placeholder="0.01~99999.99" required>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">兑换积分</label>
					<div class="controls">
						<input type="number" id="salesPrice" name="salesPrice" value="${goods.salesPrice }" max="1000000" placeholder="1~1000000.00"  onkeyup="validateUtils.replaceNotNumEmpty(this);">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">商品单位</label>
					<div class="controls">
						<select class="chzn-select" name="unitId" id="unitId"  data-placeholder="请选择" style="vertical-align:top;">
							<option value=""></option>
							<c:forEach items="${units}" var="unit">
								<option value="${unit.unitId}" <c:if test="${unit.unitId eq goods.unitId}">selected</c:if>>${unit.unitName}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">商品重量</label>
					<div class="controls">
						<input type="number" id="weight" name="weight" min="0.01"  max="99.99" placeholder="0.01~99.99" value="${goods.weight}" placeholder="商品重量" >
						单位(kg)
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">商品分类</label>
					<div class="controls">
						<input type="hidden" name="goodsTypeStr" id="goodsTypeStr" value="" />
                		<ul id="tree" class="ztree" style="width: 25% ;height:auto; "></ul>
					</div>
				</div>
				<div class="control-group" >
					<label class="control-label">商品主图</label>
					<div class="controls">
								<a id="imgBox" href="<%=httpUrl%>${goods.goodsImg}" >
								  <img src="<%=httpUrl%>${goods.goodsImg}"  style="width: 50px; height: 50px"  id="imgHref" />
	              				</a>
	              				<input type="button" class="btn btn-mini btn-danger" value="更改" onclick="changeImg()" id="chImg" />
	             				<input type="file" id="fileInput" name="fileInput" style="display:none;" />
								<input id="url" name="img" type="hidden" />
					</div>
				</div>
				<div class="control-group" >
					<label class="control-label"></label>
					<div class="controls">
	             		<font color="red">注：(图片尺寸规格320px * 320px)</font>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" >商品备注</label>
					<div class="controls">
						<textarea style="width:300px;height:40px;" name="remark" id="remark">${goods.remark}</textarea>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" >商品描述</label>
					<div class="controls">
						<script id="editor" type="text/plain" style="width:650px;height:259px;"></script>
						<textarea style="width:790px;height:259px;" name="goodsDesc" id="descp">${goods.goodsDesc }</textarea>
					</div>
				</div>
				<div align="center" style="height:40px;">
           			 <a class="btn btn-mini btn-primary" onclick="save();">保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
           			 <a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
				</div>
			</form>

		</div>
		<!-- PAGE CONTENT ENDS HERE -->
	</div><!--/row-->
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
		<!-- ztree -->
	 <link type="text/css" rel="stylesheet" href="plugins/zTree/3.5/demo.css" />
  	 <link type="text/css" rel="stylesheet" href="plugins/zTree/3.5/zTreeStyle.css" />
	 <script type="text/javascript" src="plugins/zTree/3.5/jquery.ztree.core.min.js"></script>
	 <script type="text/javascript" src="plugins/zTree/3.5/jquery.ztree.excheck.min.js"></script>
		 <!-- base64 -->
	  <script src="static/js/base64.js"></script>
	  <link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
	  <script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
	  <!-- js工具类 -->
	 <script type="text/javascript" src="static/js/common/commonutils.js"></script>
	  <!--编辑器  -->
	<script src="plugins/ckeditor/ckeditor.js"></script>
  	<script src="plugins/ckeditor/config.js"></script>
		<script type="text/javascript">
		$(top.hangge());
		
		//初始化编辑器
		CKEDITOR.replace('descp',{
			width:800,
			
		});
		
		//树的设置
		function zTreeOnClick(event,treeId,treeNode){
			//console.log(treeNode.goodsTypeId+","+treeNode.goodsTypeId);
		}
		
		  var nodeStr = '${goodsTypes}';
		  var zTreeObj,
			setting = {
				view: {
					selectedMulti: true,
				},
				callback:{
					onClick:zTreeOnClick
				},
				check:{
					enable:true,
					chkStyle:"checkbox",
					chkboxType:{"Y":"","N":""},
					
				},
				data:{
					key:{
						name:'typeName',
						//checked: "isChecked"
					}
				}
			};
		
		  $(document).ready(function(){
				zTreeObj = $.fn.zTree.init($("#tree"), setting, JSON.parse(nodeStr));
				$(top.hangge());
				//$('.date-picker').datepicker()
				});
		
		
		//上传图片
		$('#fileInput:visible').ace_file_input({
			no_file : '请选择图片 ...',
			btn_choose : '选择',
			btn_change : '更改',
			droppable : false,
			onchange : null,
			thumbnail : false,
			before_remove : function() {
				$("#url").val("");
				$("#imgBox").attr("href", "").hide();
				$("#imgHref").attr("src", "");
				return true;
			}
		});
		
		new img2base64("fileInput", "url", "imgHref", "imgBox");

		$("#imgBox").fancybox({
			helpers : {
				title : {
					type : 'inside'
				}
			}
		});
		</script>
	</body>
</html>