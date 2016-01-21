<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<head>
<%@include file="/page/msds/common/commonjs.jsp" %>
</head>
<body>
	<div class="mini-fit" style="background: url('<%=ctx%>/js/miniui/themes/blue/images/toolbar/toolbar.gif') repeat-x scroll 0 0 #f0f0f0;border: 1px solid #99bce8">
		<div id="editform" class="form">
			<input class="mini-hidden" name="id" />
			<table style="width:100%;">
#foreach($po in $!{columnDatas})
#if  ($po.columnName !='id'&&$po.columnName !='create_date'&&$po.columnName !='modify_date')
				<tr>
					<td>${po.columnComment}：</td>
					<td><input name="${po.columnName}" 
						${po.optionType} class="${po.classType}" emptyText="请填写${po.columnComment}" /></td>
				</tr>
#end
#end
				<tr>
					<td style="text-align:right;padding-top:5px;padding-right:20px;"
						colspan="3"><a class="mini-button"
						onclick="updateRow()">保存</a> <a class="mini-button"
						onclick="cancelRow()">取消</a></td>
				</tr>
			</table>
		</div>
	</div>
</body>
<script type="text/javascript" src="<%=ctx%>/${viewPackage}/${lowerName}/${lowerName}_edit.js"></script>
</html>