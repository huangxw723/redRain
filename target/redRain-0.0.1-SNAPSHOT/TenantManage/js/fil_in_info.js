$(function(){
	// 截取链接中的参数
	var getQueryString = function(name) {
	    if (name != null && name.toString().length > 1) {
	        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	        // 获取?后面的字符串处理后返回
	        var r = window.location.search.substr(1).match(reg);
	        if (r != null) return unescape(r[2]);
	        return null;
	    }
	}

  // 手机验证
  // if(!(/^1[34578]\d{9}$/.test(phone))){} // 正则验证手机号码

  // 身份证 or 驾驶证验证
  function certifi ( str ) {
    var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if(!reg.test(str)) {
      return true;
    } else {
      return false;
    }
  }

  // 港澳通行证验证
  $.extend({"isHKMacao": function(value, type) {
    var reg = /^[HMhm]{1}([0-9]{10}|[0-9]{8})$/;
    if (!reg.test(value)) {
      console.log("港澳通行证格式不正确")
      return false;
    } else {
      return true;
    }
  }});

  // 邮箱验证
  function checkEmail( str ) {
    //对电子邮件的验证
    var reg2 = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if(!reg2.test( str )) {
      //myreg.focus();
      console.log('提示\n\n请输入有效的E_mail！');
      return false;
    } else {

      console.log("邮箱正确");
      return true;

    }
  }

  /**
   * param - userType(1为商场，2为店铺)
   * null表示当前是回显页面
   */
  var addressStatu = false; // 只要选择了一次省或者市或者区，就默认让省市区显示第一个
  var sbStatus = true; // 提交状态

  if ( getQueryString("id") != null ) {
    var id = getQueryString("id");
    var type = getQueryString("uType");
    sessionStorage.setItem("token", id); // token
    sessionStorage.setItem("userType", type);
  }

  var statusId = sessionStorage.getItem("status");
  if(statusId == null) {
    statusId = getQueryString("status");
    sessionStorage.setItem("status", statusId); // 用户状态
  } else {
    statusId = sessionStorage.getItem("status"); // 用户状态
  }

  var token = Number( sessionStorage.getItem("token") ); // token
  var userType = Number( sessionStorage.getItem('userType') ); // 账号类型
  var otype = ""; // 回显页面再次提交时需要传2


  if (!isNaN(token)) { // 数字或者null
    noShowData();
    console.log("token为数字")
    console.log(token)
    if ( userType == 1 ) {
      $(".step_box").eq(2).remove();
      $(".step_box:eq(2) > .step").html(3);
      $(".step_box:last > .step").html(4);
    }
    //token = Number( token );

  } else {
    console.log("token为字符串")
    token = sessionStorage.getItem("token");

    getShowData();
    otype = 2;

  }


  // 回显信息函数
  function getShowData () {

    $.ajax({ // 商家个人信息
      url: host + 'codeMerchant/operMerchantUserInfoPage.do',
      type: 'GET',
      data: {id: token},
    }).done(function( data ) {
      var html = template("personal_info", data.data);
      $(".personal_info").html( html );

      if ( statusId == 1003 || statusId == 1007 ) {
        memoryUpload.sfzzm = '';
        memoryUpload.sfzbm = '';
        memoryUpload.yyzz = '';
        memoryUpload.jyxkz = '';
      }

      if ( statusId != null ) {

        if ( statusId == 1004 || statusId == 1005 ) { // 待审核、业主已审
          $("#seller_info, #individual_info").hide();
          $("#success").show();
          $('.between .current').removeClass('current').nextAll('div:eq(1)').addClass('current');
        }

        if ( statusId == 1007 ) { // 驳回

          $("#seller_info, #individual_info").hide();
          $("#error").show();
          $("#reject").html( data.data.reason );

            $('.between .current').removeClass('current').nextAll('div:eq(1)').addClass('current');
            $('.between .current > span').html("审批不通过");

          // 返回修改
          $(".return_revise").on("click", function () {
            $("#individual_info").fadeIn(1000);
            $("#error").hide();
            $('.between .step_box').removeClass('current').eq(0).addClass('current');
          });

        }
      }

      console.log(data);
    }).fail(function( data ) {
      console.log("error");
    });


    $.ajax({ // 商户信息
      url: host + 'codeMerchant/operMerchantPage.do',
      type: 'GET',
      data: {id: token},
    }).done(function( data ) {

      var html = template("tenant_infon", data.data);
      $(".tenant_infon").html( html );

      if(statusId != 1003) {
        memoryUpload.shlogo = '';
        memoryUpload.qrcode = '';
        var one = $(".seller_background_box input:first").attr("data-shoppicid")
        var two = $(".seller_background_box input:eq(1)").attr("data-shoppicid")
        var three = $(".seller_background_box input:eq(2)").attr("data-shoppicid")
        one == undefined ? one = "as123" : one
        two == undefined ? two = "as456" : two
        three == undefined ? three = "as789" : three
        // shback = {one: 123, 456: 456, 789: 789};
        console.log(one, two, three)
        shback[one] = ""
        shback[two] = ""
        shback[three] = ""
        console.log(shback)
        shbackStatus = true;
      }

      tradeCategoryId = data.data.asShop.tradeCategoryId
      provinceId = data.data.asShop.provinceId
      cityId = data.data.asShop.cityId
      areaId = data.data.asShop.areaId
      businessDistrictId = data.data.asShop.businessDistrictId
      storeId = data.data.asShop.storeId
      floor = data.data.asShop.floor

      data.msg == "" ? addressStatu = true : addressStatu = false;

      if ( Object.keys( data.data.asShop.region).length != 0 ){
        $(".import_number").val( data.data.asShop.region );
      }

      showDOM();

      // 反向查询地图
      ac.setInputValue(data.data.asShop.address)
      myValue = $("#suggestId").val();
      setPlace();

      console.log(data);

    }).fail(function( data ) {
      console.log("error");
    });
  }

  // 正常进入页面函数
  function noShowData () {
    console.log("现在不需要回显")
    // var html = $('#personal_info').html();
    var hd = {identityFront: "",identityBack: "", businessLicence: "",businessCertificate: ""}
    var html1 = template("personal_info", hd);
    $('.personal_info').html( html1 );

    var html2 = template("tenant_infon", {asShop: "", shopPic: []});
    $('.tenant_infon').html( html2 )

    addressStatu = true;

    if(statusId == 1004) {
      $("#seller_info, #individual_info").hide();
      $("#success").show();
      $('.between .current').removeClass('current').nextAll('div:eq(1)').addClass('current');
    }

    tradeType();
    showDOM();
  }

  // 商家和店铺需要显示的DOM
  function showDOM () {
    if ( userType == 1 ) {
      $('#shop_DOM, #market_DOM_2, #market_DOM_3, .import_number').hide();
      $('#market_DOM_1').show();
      //$(".import_address").width(880);
      $(".shop_address").hide();
    }
  }

  // 后续优化可以用辅助函数提取共用代码
  //template.helper("judge_assist", function(price, pictureUrl){
		//console.log(price, pictureUrl)
  //});

	// 隐藏所以下拉选择框第一个选项
	setTimeout(function () {
    $("select").find("option:first").hide();
  }, 2000)

  /**
   * - 获取商户名称
   * @AuthorHTL Stone Zhou
   * @DateTime  2017-7-11 11:54:47
   */
  function getCommercial () {
    $.ajax({
      url: host + "codeMerchant/getStoreAll.do",
      type: "GET"
    }).done(function ( data ) {
      var str = "";
      if ( data.status == 0 ) {
        $.each(data.data, function(index, val) {
          str += '<option value="' + val.storeId + '">' + val.name + '</option>'
        });
        $('.commercialName').html("");
        $('.commercialName').html( str );


        $(".trade_type option").each(function(index, el){
          if ( $(el).val() == tradeCategoryId ) {
            $(this).attr("selected", "selected")
          }
        });

        $(".commercialName option").each(function(index, el){
          if ( $(el).val() == storeId ) {
            $(this).attr("selected", "selected")
          }
        });
      }
      console.log(data)
    }).fail(function (data) {
      console.log(data)
    });
  }

	/*
     * - 图片上传
     *  @DateTime 2017-6-26 10:27:03
     *  param - memoryUpload (存储上传的base64)
     *  param - shbackground(商户背景图)
     *  param - .......
     * */
    var memoryUpload = {}, shback = {}, shbackStatus = false;
	  var	tradeCategoryId = null, provinceId = null, cityId = null, areaId = null, businessDistrictId = null, storeId = null, floor = null;
    var h5_upload = {
        init: function( target ){
        	this.target = target;
            this.eventBind();
        },
        eventBind: function(){
            var that = this;
            this.target.change(function( event ){
            	event.stopPropagation();
                var reader = new FileReader();
                reader.onload = function ( event ) {
                    that.compress(this.result); // base64
                };
                reader.readAsDataURL(this.files[0]);
            });
        },
        compress: function ( res ) { // 压缩base64
            var that = this;
            var img = new Image(),
                    maxH = 300;
            img.onload = function () {
                var cvs = document.createElement('canvas'),
                        ctx = cvs.getContext('2d');
                if(img.height > maxH) {
                    img.width *= maxH / img.height;
                    img.height = maxH;
                }
                cvs.width = img.width;
                cvs.height = img.height;

                ctx.clearRect(0, 0, cvs.width, cvs.height);
                ctx.drawImage(img, 0, 0, img.width, img.height);

                var dataUrl = cvs.toDataURL();//'image/jpeg', 1.0

                if ( dataUrl.includes('base64,') ){
                    dataUrl = dataUrl.slice(dataUrl.indexOf('base64,') + 7);
                }
                that.upload( dataUrl ); // 调用上传方法，参数base64
            };
            img.src = res;
        },
        upload: function( base64Img ){
            /* 这里写上传方法,图片流是base64_encode的 */
            // 等待页面渲染完成再把图片显示到页面上
            // flag = 1(身份证) flag = 2(营业执照) flag = 3(经营许可证) ......
            var flag = this.target.attr('data-flag');
            var shopPicId;
            this.target.siblings('img').remove(); // 重新选择上传时删除之前的
            this.target.after("<img class='img_wrap' src="+ 'data:image/png;base64,' +  base64Img + ">"); // 预览
            switch ( flag ) {
            	case "1": // 申请人证件照(正面)
            	    memoryUpload.sfzzm = base64Img;
            		break;
            	case "-1": // 申请人证件照(背面)
            	    memoryUpload.sfzbm = base64Img;
            		break;
            	case "2": // 营业执照
            	    memoryUpload.yyzz = base64Img;
            		break;
            	case "3": // 经营许可证
            	    memoryUpload.jyxkz = base64Img;
            		break;
            	case "4": // 商户logo
            	    memoryUpload.shlogo = base64Img;
            		break;
            	case "5-1": // 商户背景图1
            		//shback.one = base64Img;
                shopPicId = this.target.attr("data-shoppicid");
                this.target.parent().addClass("remove_img")
                shopPicId == undefined ? shback["as123"] = base64Img : shback[shopPicId] = base64Img;
                shbackStatus = true;
            		break;
            	case "5-2": // 商户背景图2
                shopPicId = this.target.attr("data-shoppicid");
                this.target.parent().addClass("remove_img")
                shopPicId == undefined ? shback["as456"] = base64Img : shback[shopPicId] = base64Img;
                shbackStatus = true;
            		break;
            	case "5-3": // 商户背景图3
                shopPicId = this.target.attr("data-shoppicid");
                this.target.parent().addClass("remove_img")
                shopPicId == undefined ? shback["as789"] = base64Img : shback[shopPicId] = base64Img;
                shbackStatus = true;
            		break;
            	default: // 商户二维码
            	    memoryUpload.qrcode = base64Img;
            		break;
            }
            console.log(memoryUpload)
            console.log(shback)
        },
        target: undefined
    };
	
	// 点击上传图片
    $(document).on("click", '.upload', function( event ){
    	event.stopPropagation();
    	h5_upload.init( $(this) )
    });

    // 删除店铺图片
    $(document).on("click", ".remove_img", function () {
      var shopPicId = $(this).children("input").attr("data-shoppicid");
      var _this = this;

      $.ajax({
        url: host + "codeMerchant/deleteMainPic.do",
        type: 'GET',
        data: {shopPicId: shopPicId},
        success: function (data) {
          if(data.status == 0) {
            $(_this).removeClass("remove_img").children("img").remove();

            if ($(".seller_background_box img").length == 0) {
              shbackStatus = false;
            }
            console.log(shbackStatus)
          }
          console.log(data)
        },
        error: function (data) {
          console.log("error")
        }
      });

      var flag = $(this).children("input").attr("data-flag");

      switch ( flag ) {
        case "5-1":
          shback[shopPicId] == undefined? shback["as123"] = "" : delete shback[shopPicId];
          $(this).children("input").attr("data-shoppicid", "as123");
          $(_this).removeClass("remove_img").children("img").remove();
          break;
        case "5-2":
          shback[shopPicId] == undefined? shback["as456"] = "" : delete shback[shopPicId];
          $(this).children("input").attr("data-shoppicid", "as456");
          $(_this).removeClass("remove_img").children("img").remove();
          break;
        case "5-3":
          shback[shopPicId] == undefined? shback["as789"] = "" : delete shback[shopPicId];
          $(this).children("input").attr("data-shoppicid", "as789");
          $(_this).removeClass("remove_img").children("img").remove();
          break;
      }
    });

	/**
	 * name: 行业类型
	 * @AuthorHTL Stone Zhou
	 * @DateTime  2017-6-28 16:18:09
	 */
    function tradeType (){
      var str = '';

      switch ( userType ) {

        case 1:

          var type = 5;
          break;

        case 2:

          var type = 1;
          break;

        default:
          console.log("获取行业类型参数出错");
      }

      $.ajax({
        url: host + 'codeMerchant/getBaseType.do',
        type: 'GET',
        data: {type: type}
      }).done(function( data ) {
          if ( data.status == 0 ) {
            $.each(data.data, function(index, val) {
              str += '<option value="' + val.id + '">' + val.name + '</option>'
            });
            $('.trade_type, #market_type').html("");
            $('.trade_type, #market_type').append( str );

            $(".trade_type option").each(function(index, el){
              if ( $(el).val() == tradeCategoryId ) {
                $(this).attr("selected", "selected")
              }
            });

            $("#market_type option").each(function(index, el){
              if ( $(el).val() == tradeCategoryId ) {
                $(this).attr("selected", "selected")
              }
            });
          }
        console.log(data);
      }).fail(function( data ) {
        console.log("error");
      });
    };
    

 	/**
	 * name: 获取省份
	 * @AuthorHTL Stone Zhou
	 * @DateTime  2017-6-27 16:22:47
	 */
    function getProvince (){
    	var str = '';
    	$.ajax({
    		url: host + 'codeMerchant/getProvince.do',
    		data: {data: userType}
    	}).done(function( data ) {
    		if ( data.status == 0 ) {
	    		$.each(data.data, function(index, val) {
	    			str += '<option value="' + val.provinceId + '">' + val.provinceName + '</option>'
	    		});
	    		$('.province').append( str );

          $(".province option").each(function(index, el){
            if ( $(el).val() == provinceId ) {
	    				$(this).attr("selected", "selected")
	    			}
	    		});

          if ( provinceId!=null && JSON.stringify(provinceId) != "{}" ) getCity(provinceId);

          //if ( !addressStatu ) {
          //  $(".province option").eq(1).attr("selected", "selected")
          //}

    		}
    		console.log(data);
    	}).fail(function( data ) {
    		console.log("error");
    	});
    };

	/**
	 * name: 获取城市
	 * @AuthorHTL Stone Zhou
	 * @DateTime  2017-6-28 14:22:10
	 * @param - provinceId (省份id) type: number
	 */
    function getCity ( id ){
    	var str = '';
		$.ajax({
			url: host + 'codeMerchant/getCity.do',
			type: 'GET',
			data: { provinceId: id },
		}).done(function( data ) {
			if ( data.status == 0 ) {
				$('.city').html(''); // 清空之前数据
				$.each(data.data, function(index, val) {
					str += '<option value="' + val.cityId + '">' + val.cityName + '</option>'
				});
				// console.log(str)
				$('.city').append(str);
        //if (Object.keys(cityId).length == 0) {
        //  cityId = 72;
        //}
				$(".city option").each(function(index, el){
					if ( $(el).val() == cityId ) {
						$(this).attr("selected", "selected");
					}
				});
        console.log(data)
        addressStatu ? getArea(data.data[0].cityId) : getArea(cityId);

			}
			console.log( data );
		}).fail(function( data ) {
			console.log("error");
		});
		
    };

	/**
	 * name: 获取区
	 * @AuthorHTL Stone Zhou
	 * @DateTime  2017-6-28 14:42:24
	 * @param - cityId (城市id) type: number
	 */
    function getArea ( id ){
    	var str = '';
    	$.ajax({
    		url: host + 'codeMerchant/getArea.do',
    		type: 'GET',
    		data: {cityId: id},
    	}).done(function( data ) {
    		if ( data.status == 0 ) {
    			$('.area').html(''); // 清空之前数据
    			$.each(data.data, function(index, val) {
    				str += '<option value="' + val.areaId + '">' + val.areaName + '</option>'
    			});
    			$('.area').append(str);
    			$(".area option").each(function(index, el){
    				if ( $(el).val() == areaId ) {
    					$(this).attr("selected", "selected");
    				}
    			});

          if ( data.data.length == 0 ) {
            $(".area").html('<option value="没有区县可以选择">没有区县可以选择</option>')
          } else {
            addressStatu ? getTradingArea(areaId) : getTradingArea(areaId);
          }
    		}
    		console.log(data);
    	}).fail(function( data ) {
    		console.log("error");
    	})
    };

	/**
	 * name: 获取商圈
	 * @AuthorHTL Stone Zhou
	 * @DateTime  2017-6-28 14:51:03
	 * @param - areaId (区id) type: number
	 */
    function getTradingArea ( id ){
		var str = '<option value="false">请选择商圈</option>';
		$.ajax({
			url: host + 'codeMerchant/getBusinessDistrict.do',
			type: 'GET',
			data: {areaId: id},
		}).done(function( data ) {
			if ( data.status == 0 ) {
				$('.trading_area').html(''); // 清空之前数据
				$.each(data.data, function(index, val) {
					str += '<option value="' + val.businessDistrictId + '">' + val.name + '</option>'
				});
				$('.trading_area').append(str);

				$(".trading_area option").each(function(index, el){
					if ( $(el).val() == businessDistrictId ) {
						$(this).attr("selected", "selected")
					}
				});

				if ( data.data.length == 0 ) {
          $('.trading_area').html('<option value="false">暂无数据</option>');
          $('.stroe').html('<option value="false">暂无数据</option>');
        } else {
          addressStatu ? getStore(businessDistrictId) : $(".trading_area option:eq(0)").attr("selected", "selected");
        }

      }
			console.log(data);
		}).fail(function( data ) {
			console.log("error");
		});
    };

	/**
	 * name: 获取商场
	 * @AuthorHTL Stone Zhou
	 * @DateTime  2017-6-28 14:58:18
	 * @param - businessDistrictId (商圈id) type: number
	 */
    function getStore ( id ){
		var str = '<option value="">请选择商场</option>';
		$.ajax({
			url: host + 'codeMerchant/getStore.do',
			type: 'GET',
			data: {businessDistrictId: id},
		}).done(function( data ) {
			if ( data.status == 0 ) {
				$('.stroe').html(''); // 清空之前数据
				$.each(data.data, function(index, val) {
					str += '<option value="' + val.storeId + '">' + val.name + '</option>'
				});
				$('.stroe').append(str);

				$(".stroe option").each(function(index, el){
					if ( $(el).val() == storeId ) {
						$(this).attr("selected", "selected")
					}
				});
        getStorey();

				//if ( data.data.length == 0 ) {
				//	$('.stroe').html('<option value="没有商场可以选择">没有商场可以选择</option>');
				//}
			}
			console.log(data);
		}).fail(function( data ) {
			console.log("error");
		});
    };

	/**
	 * name: 获取楼层
	 * @AuthorHTL Stone Zhou
	 * @DateTime  2017-6-28 15:24:23
	 */
    function getStorey (){
    	var str = '';
		$.ajax({
			url: host + 'codeMerchant/getFloor.do',
			type: 'GET'
		}).done(function( data ) {
			if ( data.status == 0 ) {
				$('.storey').html(''); // 清空之前数据
				var arr = data.data;
				// 排序 从小到大
				arr.sort(function(a, b){
					return a - b;
				});
				$.each(arr, function(index, val) {
					str += '<option value="' + val + '">' + val + '</option>'
				});
				$('.storey').append(str);
				$(".storey option").each(function(index, el){
					if ( $(el).val() == floor ) {
						$(this).attr("selected", "selected")
					}
				});
			}
			console.log(data);
		}).fail(function( data ) {
			console.log("error");
		});
    };
    
	

	// 第一步 => 保存并下一步
	$(document).on('click', '#submit', function(){

    //getCommercial();
    //$('#individual_info').slideUp(500);
    //$('#seller_info').slideDown(500);
    //$('.between .current').removeClass('current').next('div').addClass('current');
    //tradeType();
    //getProvince();
    //return;

    var sqrxm = $("#sqrxm").val();
    var sqrDatum = $(".sqr_select").val();
    var fr_select = $('.fr_select').val();
    var frName = $('#frxm').val();
    var zjh = $('#zjh').val();
    var frZjh = $('#frzjh').val();

    // 申请人姓名
    if ($.trim(sqrxm) == "") {
      webToast('请输入申请人姓名', "middle", 1000);
      return false;
    }

    // 证件类型
    if (sqrDatum == "false") {
      webToast('请选择证件类型', "middle", 1000);
      return false;
    }

    // 法人证件类型
    if (fr_select == "false") {
      webToast('请选择法人证件类型', "middle", 1000);
      return false;
    }

    // 证件号
    if ($.trim(zjh) == "") {
      webToast('请输入证件号', "middle", 1000);
      return false;
    }

    // 法人姓名
    if ($.trim(frName) == "") {
      webToast('请输入法人姓名', "middle", 1000);
      return false;
    }

    // 法人证件号
    if ($.trim(frZjh) == "") {
      webToast('请输入法人证件号码', "middle", 1000);
      return false;
    }

    // 身份证正面
    if (memoryUpload.sfzzm == undefined) {
      webToast('请上传身份证正面', "middle", 1000);
      return false;
    }

    // 身份证背面
    if (memoryUpload.sfzbm == undefined) {
      webToast('请上传身份证背面', "middle", 1000);
      return false;
    }

    // 营业执照
    if (memoryUpload.yyzz == undefined) {
      webToast('请上传营业执照', "middle", 1000);
      return false;
    }

    //经营许可证
    if (memoryUpload.jyxkz == undefined) {
      webToast('请上传经营许可证', "middle", 1000);
      return false;
    }

    // 验证申请人信息
    if ( sqrDatum == 1 || sqrDatum == 3 ) { // 身份证和驾驶证

      if ( certifi( $("#zjh").val() ) ) {
        sqrDatum == 1 ? webToast('身份证格式不正确', "middle", 1000) : webToast('驾驶证格式不正确', "middle", 1000);
        return false;
      }

    } else if ( sqrDatum == 2 ) { // 港澳通行证

      if ( !$.isHKMacao($("#zjh").val()) ) {
        webToast('港澳通行证格式不正确', "middle", 1500);
        return false;
      }

    }

    // 法人证件类型
    if ( fr_select == 1 || fr_select == 3 ) {

      if ( certifi( $("#frzjh").val() ) ) {
        fr_select == 1 ? webToast('身份证格式不正确', "middle", 1000) : webToast('驾驶证格式不正确', "middle", 1000);
        return false;
      }

    } else if ( fr_select == 2 ) { // 港澳通行证

      if ( !$.isHKMacao($("#frzjh").val()) ) {
        webToast('港澳通行证格式不正确', "middle", 1000);
        return false;
      }

    }

    /**
     * - 商户个人信息 资料（新增|修改）-黄旭文
     * @AuthorHTL Stone Zhou
     * @DateTime  2017-06-26T10:38:16+0800
     */
    if ( sbStatus ) {

      sbStatus = false;

      $.ajax({
        type: "POST",
        url: host + 'codeMerchant/operMerchantUserInfo.do',
        beforeSend: function (xhr) {
          $(".loader").show(); // 数据加载成功之前，使用loading组件
        },
        data: {
          token: token,
          applicant: sqrxm, // 申请人姓名
          applicantCertificateType: sqrDatum, // 申请人证件类型
          applicantCertificateNum: zjh, // 申请人证件号
          corporation: $('#frxm').val(), // 法人姓名
          corporationCertificateType: $('.fr_select').val(), // 法人证件类型
          corporationCertificatenNum: frZjh, // 法人证件号码
          identityFront: memoryUpload.sfzzm, // 身份证正面
          identityBack: memoryUpload.sfzbm, // 身份证背面
          businessLicence: memoryUpload.yyzz, // 营业执照
          businessCertificate: memoryUpload.jyxkz, // 经营许可证
          otype: otype
        },
      }).done(function( data ) {

        setTimeout(function () {
          sbStatus = true;
        }, 1500);

        $(".loader").hide();

        if ( data.status == 0 ) {

          $('#individual_info').slideUp(500);
          $('#seller_info').slideDown(500);
          $('.between .current').removeClass('current').nextAll('div:eq(0)').addClass('current');
          getProvince();
          tradeType();
          getCommercial();

        } else {

          webToast(data.msg, "middle", 1000);

        }

        console.log(data);

      }).fail(function( data ) {

        sbStatus = true;
        $(".loader").hide();
        console.log("error");

      });

    }


	});

	// 第二步 => 返回第一步
	$('#return_to_top').on('click', function(){
		$('#seller_info').slideUp(500);
		$('#individual_info').slideDown(500);
		$('.between .current').removeClass('current').prev('div').addClass('current');
	});

	// 第二步 => 提交审核
	$('#verify').on('click', function(){
		$('#popup').fadeIn(500);
		$('#masklayer').fadeIn(500);
	});

	// 模态框 => 返回修改
	$('.pop_return').on('click', function( event ){
		event.preventDefault();
		$('#popup').fadeOut(500);
		$('#masklayer').hide();
	});

	// 模态框 => 确定
	$('.pop_confirm').on('click', function( event ){

		event.preventDefault();

    var phone = $(".contact_tel").val();
    var trade_type = $('.trade_type').val();
    var email = $(".contact_mail").val();
    var provinceId = $('.province').val();
    var cityId = $('.city').val();
    var areaId = $('.area').val();
    var tradingId = $('.trading_area').val();
    var stroeId = $('.stroe').val();
    var storeyId = $('.storey').val();
    var shopName = $("#shopName").val();
    var contacts = $('.contacts').val();
    var suggestId = $('#suggestId').val();

    var mdb = $("#masklayer, #popup");

    // 商户Logo
    if ( memoryUpload.shlogo == undefined ) {
      mdb.hide();
      webToast("请上传Logo", "middle", 1000);
      return false;
    }

    // 商家背景图
    if ($(".seller_background_box img").length == 0) {
      console.log(shbackStatus)
      mdb.hide();
      webToast("请至少上传一张背景图", "middle", 1000);
      return false;
    }

    // 商户二维码
    if ( memoryUpload.qrcode == undefined ) {
      mdb.hide();
      webToast("请上传二维码", "middle", 1000);
      return false;
    }

    // 验证联系人
    if ( $.trim(contacts) == "" ) {
      mdb.hide();
      webToast("联系人不能为空", "middle", 1000);
      return false;
    }

    // 验证手机
    var regPhone = phone.substr(0, 1);
    console.log(phone)
    if ( regPhone == 1 ) {

      if ( !(/^1[34578]\d{9}$/.test( phone )) ) {
        mdb.hide();
        webToast("请输入正确的手机号码", "middle" ,1000);
        return false;
      }

    } else {

      if ( !(/^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,14}$/.test(phone)) ) {
        mdb.hide();
        webToast("请输入正确的号码", "middle" ,1000);
        return false;
      }

    }

    // 验证邮箱
    if ( !checkEmail( email ) ) {
      mdb.hide();
      webToast("请输入正确的邮箱", "middle" ,1000);
      return false;
    }

    // 商场需要验证的商户
    if ( userType == 1 ) {

      // 验证省份
      if ( $.trim( $(".commercialName").val() ) == "请选择商场" ) {
        mdb.hide();
        webToast("请选择商场", "middle" ,1000);
        return false;
      }

      // 行业类型
      if ( $.trim( $('#market_type').val() ) == "请选择类型" ) {
        mdb.hide();
        webToast("请选择类型", "middle" ,1000);
        return false;
      }

    } else {

      // 验证商户名称
      if ( $.trim(shopName) == "" ) {
        mdb.hide();
        webToast("商户名称不能为空", "middle", 1000);
        return false;
      }

      // 验证行业类型
      if ( $.trim( trade_type ) == "请选择行业类型" ) {
        mdb.hide();
        webToast("请选择行业类型", "middle", 1000);
        return false;
      }

      // 验证省份
      if ( $.trim( provinceId ) == "请选择省份" ) {
        mdb.hide();
        webToast("请选择省份", "middle" ,1000);
        return false;
      }

      // 验证城市
      if ( $.trim( cityId ) == "请选择城市" ) {
        mdb.hide();
        webToast("请选择城市", "middle" ,1000);
        return false;
      }

      // 验证区县
      if ( $.trim( areaId ) == "" ) {
        mdb.hide();
        webToast("请选择区或县", "middle" ,1000);
        return false;
      }

      // 验证商圈
      if ( $.trim( tradingId ) == "请选择商圈" || tradingId == "false" ) {
        mdb.hide();
        webToast("请选择商圈", "middle" ,1000);
        return false;
      }

      // 验证商场
      // if ( $.trim( stroeId ) == "" || stroeId == "false"  ) {
      //   mdb.hide();
      //   webToast("请选择商场", "middle" ,1000);
      //   return false;
      // }

      // 验证楼层
      if ( $.trim( storeyId ) == "请选择楼层" || storeyId == "" ) {
        mdb.hide();
        webToast("请选择楼层", "middle" ,1000);
        return false;
      }

      // 验证详细地址
      if ( $.trim( suggestId ) == "" ) {
        mdb.hide();
        webToast("请输入详细地址", "middle" ,1000);
        return false;
      }

    }

    /**
     * name: 商场/店铺资料提交
     * @AuthorHTL Stone Zhou
     * @DateTime  2017-6-28 17:38:09
     * @param ......
     */
		if ( userType == 2 ) { // 店铺

			var data = {
				shopName: shopName, // 店铺名称

				tradeCategoryId: trade_type, // 行业类型

				logoUrl: memoryUpload.shlogo, // 商户logo

				mainPicUrl: JSON.stringify([shback]), // 店铺背景图

				qrCodeUrl: memoryUpload.qrcode, // 商户二维码

				contact: contacts, // 联系人

				tel: phone, // 联系电话

				contactMail: email, // 联系邮箱

				provinceId: Number( provinceId ), // 省份id..

				cityId: Number( cityId ), // 城市id..

				areaId: Number( areaId ), // 区域id..

				businessDistrictId: Number( tradingId ), // 商圈id..

				storeId: Number( stroeId ), // 商场id..

				floor: Number( storeyId ), // 楼层

				address: suggestId, // 详细地址

				token: token, // 用户信息

				otype: otype, // 回显信息再次提交时传2

        region: $(".import_number").val(), // 店铺编号

				lat: sessionStorage.getItem('lat'),

				lng: sessionStorage.getItem('lng')
			}

		} else if ( userType == 1 ) { // 商场
			var data = {
        storeId: parseInt( $(".commercialName").val() ), // 商场id

				shopName: $('.commercialName option:selected').text(), // 商场名称

				tradeCategoryId: $('#market_type').val(), // 行业类型

				logoUrl: memoryUpload.shlogo, // 商户logo

				mainPicUrl: JSON.stringify([shback]), // 店铺背景图

				qrCodeUrl: memoryUpload.qrcode, // 商户二维码

				contact: $('.contacts').val(), // 联系人

				tel: phone, // 联系电话

				contactMail: $('.contact_mail').val(), // 联系邮箱

				token: token, // 用户信息

				otype: otype // 回显信息再次提交时传2
			}
		}

    if ( sbStatus ) {

      sbStatus = false;

      $.ajax({
        url: host + 'codeMerchant/operSaveMerchant.do',
        type: 'POST',
        data: data,
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        beforeSend: function (xhr) {
          $(".loader").show(); // 数据加载成功之前，使用loading组件
        }
      }).done(function( data ) {

        setTimeout(function () {
          sbStatus = true;
        }, 1500);

        $(".loader").hide();

        if ( data.status == 0 ) {

          $('#popup').fadeOut(500);
          $('#seller_info').fadeOut(500);
          $('#success').fadeIn(800);
          $('#masklayer').hide();
          $('.between .current').removeClass('current').nextAll('div:eq(0)').addClass('current');
          $(".between > div:eq(2) > span").html("等待审核结果");

          sessionStorage.removeItem("token");
          sessionStorage.removeItem("userType");
          sessionStorage.setItem("status", 1004);
        }

        if ( data.status == 1001 ) {
          $("#masklayer, #popup").hide();
          webToast(data.msg, "middle" ,1000);
        }

        console.log(data);

      }).fail(function( data ) {

        sbStatus = true;
        $(".loader").hide();
        console.log("error");

      });

    }




	});

	// 选择省份
	$(document).on("change", 'select.province', function(event) {
		getCity($(this).val()); // param (省份id)
    addressStatu = true;
    provinceId = undefined
    cityId = undefined
    areaId = undefined
    businessDistrictId = undefined
    storeId = undefined
	});

	// 选择市
	$(document).on("change", 'select.city', function(event) {
		getArea($(this).val()); // param (市id)
    addressStatu = true;
    provinceId = undefined
    cityId = undefined
    areaId = undefined
    businessDistrictId = undefined
    storeId = undefined
	});

	// 选择区
	$(document).on("change", 'select.area', function(event) {
		getTradingArea($(this).val()); // param (区id)
    addressStatu = true;
    provinceId = undefined
    cityId = undefined
    areaId = undefined
    businessDistrictId = undefined
    storeId = undefined
	});

	// 选择商圈
	$(document).on("change", 'select.trading_area', function(event) {
		getStore($(this).val()); // param (商圈id)
    addressStatu = true;
    provinceId = undefined
    cityId = undefined
    areaId = undefined
    businessDistrictId = undefined
    storeId = undefined
	});

});
