<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="<%=basePath%>">
    <!-- jsp文件头和头部 -->
    <%@ include file="../../system/admin/top.jsp" %>
</head>
<body>
<div class="container-fluid" id="main-container">
    <div id="page-content" class="clearfix">
        <div class="row-fluid">
            <div class="row-fluid">
                <!-- 检索  -->
                <form action="business/cooperationList.do" method="post" name="Form" id="Form">
                    <input type="hidden" name="storeId" value="${storeId}">
                    <table>
                        <tr>
							
							<td style="vertical-align:top;"> 
			                  <select class="chzn-select" name="provinceId" id="provinceId" data-placeholder="请选择省份" style="vertical-align:top;width: 150px;">
			                    <option value=""></option>
			                    <option value="">全部</option>
			                    <c:forEach items="${provinceList}" var="province">
			                      <option value="${province.provinceId }" <c:if test="${pd.provinceId==province.provinceId}">selected</c:if>>${province.provinceName }</option>
			                    </c:forEach>
			                  </select>
			                </td>
			                <td style="vertical-align:top;"> 
			                  <select class="chzn-select" name="cityId" id="cityId" data-placeholder="请选择城市" style="vertical-align:top;width: 160px;">
			                  </select>
			                </td>
							
                            <td>
			                  <span class="input-icon">
			                    <input autocomplete="off" id="nav-search-input" type="text" name="keyWord" value="${pd.keyWord}"
			                           placeholder="商场编号/商场名称"/>
			                    <i id="nav-search-icon" class="icon-search"></i>
			                  </span>
                            </td>
                            <td style="vertical-align:top;">
                                <button class="btn btn-mini btn-light" onclick="search();" title="检索">
                                    <i id="nav-search-icon" class="icon-search"></i>
                                </button>
                            </td>
                        </tr>
                    </table>
                    <!-- 检索  -->


                    <table id="table_report" class="table table-striped table-bordered table-hover">
                        <thead>
                        <tr>
                            <th class="center">
                                <label>
                                    <input type="checkbox" id="zcheckbox"/>
                                    <span class="lbl"></span>
                                </label>
                            </th>
                            <th class="center">序号</th>
                            <th class="center">商场编号</th>
                            <th class="center">商场名称</th>
                            <th class="center">商场位置</th>

                        </tr>
                        </thead>

                        <tbody>

                        <!-- 开始循环 -->
                        <c:choose>
                            <c:when test="${not empty storeList}">
                                    <c:forEach items="${storeList}" var="var" varStatus="vs">
                                        <tr>
                                            <td class='center' style="width: 30px;">
                                                <label>
                                                    <input type='checkbox' name='ids' value="${var.cooperationId}"/>
                                                    <span class="lbl"></span>
                                                </label>
                                            </td>
											<td class='center' style="width: 30px;">${vs.index+1}</td>
                                            <td class='center' style="text-align:center;">${var.unactiveId}</td>
                                            <td class='center' style="text-align:center;">${var.store.name}</td>
                                            <td class='center' style="text-align:center;">${var.store.address}(${var.store.lng},${var.store.lat})</td>
                                        </tr>

                                    </c:forEach>
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
                                <td style="vertical-align:top;">
                                    <a class="btn btn-small btn-success" onclick="add();">新增</a>&nbsp;
									<a class="btn btn-small btn-danger" onclick="commit('确定要删除选中的数据吗?');" title="批量删除" ><i class='icon-trash'></i></a>
                                </td>

                                <td style="vertical-align:top;">
                                    <div class="pagination" style="float: right;padding-top: 0px;margin-top: 0px;">${page.pageStr}</div>
                                </td>
                            </tr>
                        </table>
                    </div>


                </form>
            </div>
            <!-- PAGE CONTENT ENDS HERE -->
        </div>
        <!--/row-->
    </div>
    <!--/#page-content-->
</div>
<!--/.fluid-container#main-container-->

<!-- 返回顶部  -->
<a href="#" id="btn-scroll-up" class="btn btn-small btn-inverse">
    <i class="icon-double-angle-up icon-only"></i>
</a>

<!-- 引入 -->
<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
<script src="static/js/bootstrap.min.js"></script>
<script src="static/js/ace-elements.min.js"></script>
<script src="static/js/ace.min.js"></script>

<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script>
<!-- 下拉框 -->
<script type="text/javascript" src="static/js/bootbox.min.js"></script>
<!-- 确认窗口 -->
<script type="text/javascript" src="static/js/jquery.tips.js"></script>
<!--提示框-->
<script type="text/javascript">

	 $(top.hangge());
	 
	 //修改
     function add(){
         top.mainFrame.tab.add( {
             id :'addCooperation'+"${storeId}",
             title :"添加战略伙伴",
             url :'<%=basePath%>business/addCooperation.do?storeId='+"${storeId}",
             isClosed :true,
             closeFn : function(iframe){
                 if(iframe.contents().find("#zhongxin").css('display') == 'none'){
                     if(typeof(nextPage) == "function") {
                         nextPage(${page.currentPage});
                     } else {
                         location.reload();
                     }
                 }
             }
         });
     }
	 
	 
    $(function () {
        if ($("#provinceId").val()) {
            var provinceId = $("#provinceId").val();
            var citySelect = $("#cityId");
            citySelect.empty().append("<option value=''></option>").append("<option value=''>全部</option>");
            var cityList = '${cityList}';
            var cityId = '${pd.cityId}';
            $.each(JSON.parse(cityList), function (i, list) {
                var parentId = list.provinceId;
                if (parentId == provinceId) {
                    var value = list.cityId;
                    var option = list.cityName;
                    var label = $("<option value='" + value + "'>" + option + "</option>");
                    if (cityId == list.cityId) {
                        label.attr("selected", "selected");
                    }
                    citySelect.append(label);
                }
            });
        }

        //单选框
        $(".chzn-select").chosen();
        $(".chzn-select-deselect").chosen({allow_single_deselect: true});

        //联动select事件
        $("#provinceId").change(function () {
            var provinceId = $("#provinceId").val();
            var citySelect = $("#cityId");
            citySelect.empty().append("<option value=''></option>").append("<option value=''>全部</option>");
            var cityList = '${cityList}';
            $.each(JSON.parse(cityList), function (i, list) {
                var parentId = list.provinceId;
                if (parentId == provinceId) {
                    var value = list.cityId;
                    var option = list.cityName;
                    var label = $("<option value='" + value + "'>" + option + "</option>");
                    citySelect.append(label);
                }
            });
            citySelect.trigger("liszt:updated");
        });
    });

    //检索
    function search() {
        top.jzts();
        $("#Form").submit();
    }


</script>

<script type="text/javascript">
    $(function () {
        //复选框
        $('table th input:checkbox').on('click', function () {
            var that = this;
            $(this).closest('table').find('tr > td:first-child input:checkbox')
                    .each(function () {
                        this.checked = that.checked;
                        $(this).closest('tr').toggleClass('selected');
                    });

        });

    });

    //检索
    function search(){
        top.jzts();
        $("#userForm").submit();
    }

    function commit(msg) {
        bootbox.confirm(msg, function (result) {
            if (result) {
                var str = '';
                for (var i = 0; i < document.getElementsByName('ids').length; i++) {
                    if (document.getElementsByName('ids')[i].checked) {
                        if (str == '') str += document.getElementsByName('ids')[i].value;
                        else str += ',' + document.getElementsByName('ids')[i].value;
                    }
                }
                if (str == '') {
                    bootbox.dialog("您没有选择任何内容!",
                            [
                                {
                                    "label": "关闭",
                                    "class": "btn-small btn-success",
                                    "callback": function () {
                                        //Example.show("great success");
                                    }
                                }
                            ]
                    );

                    $("#zcheckbox").tips({
                        side: 3,
                        msg: '点这里全选',
                        bg: '#AE81FF',
                        time: 8
                    });

                    return;
                } else {
                        $.ajax({
                            type: "POST",
                            url: '<%=basePath%>business/deleteCooperationIds.do',
                            data: {ids: str, storeId: ${pd.storeId}},
                            dataType: 'json',
                            //beforeSend: validateData,
                            cache: false,
                            success: function (data) {

                                console.log("result ＝　"　+ JSON.stringify(data));
                                if (data.msg == 'ok') {
                                    history.go(0);
                                    if (typeof(nextPage) == "function") {
                                        nextPage(${page.currentPage});
                                    }
                                }
                            }
                        });
                }
            }
        });

    }
</script>

</body>
</html>

