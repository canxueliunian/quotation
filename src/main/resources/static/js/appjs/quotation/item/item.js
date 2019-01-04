var prefix = "/quotation/item"
$(function () {
    load();
});

function load() {
    $('#exampleTable')
        .bootstrapTable(
            {
                method: 'get', // 服务器数据的请求方式 get or post
                url: prefix + "/list", // 服务器数据的加载地址
                //	showRefresh : true,
                //	showToggle : true,
                //	showColumns : true,
                iconSize: 'outline',
                toolbar: '#exampleToolbar',
                striped: true, // 设置为true会有隔行变色效果
                dataType: "json", // 服务器返回的数据类型
                pagination: true, // 设置为true会在底部显示分页条
                singleSelect: false, // 设置为true将禁止多选
                // contentType : "application/x-www-form-urlencoded",
                // //发送到服务器的数据编码类型
                pageSize: 10, // 如果设置了分页，每页数据条数
                pageNumber: 1, // 如果设置了分布，首页页码
                //search : true, // 是否显示搜索框
                showColumns: false, // 是否显示内容下拉框（选择显示的列）
                sidePagination: "server", // 设置在哪里进行分页，可选值为"client" 或者 "server"
                queryParamsType: "",
                // //设置为limit则会发送符合RESTFull格式的参数
                queryParams: function (params) {
                    return {
                        //说明：传入后台的参数包括offset开始索引，limit步长，sort排序列，order：desc或者,以及所有列的键值对
                        pageNumber: params.pageNumber,
                        pageSize: params.pageSize
                        // name:$('#searchName').val(),
                        // username:$('#searchName').val()
                    };
                },
                // //请求服务器数据时，你可以通过重写参数的方式添加一些额外的参数，例如 toolbar 中的参数 如果
                // queryParamsType = 'limit' ,返回参数必须包含
                // limit, offset, search, sort, order 否则, 需要包含:
                // pageSize, pageNumber, searchText, sortName,
                // sortOrder.
                // 返回false将会终止请求
                responseHandler: function (res) {
                    console.log(res);
                    return {
                        "total": res.data.total,//总数
                        "rows": res.data.records   //数据
                    };
                },
                columns: [
                    {
                        checkbox: true
                    },

                    {
                        field: 'itemname',
                        title: '项目名称',


                    },
                    {
                        field: 'packagetype',
                        title: '项目类型',
                        formatter: function (value, row, index) {
                            if (row['packagetype'] == 1) {
                                return '基础包';
                            }
                            if (row['packagetype'] == 2) {
                                return '单选包';
                            }
                            if (row['packagetype'] == 3) {
                                return '多选包';
                            }
                            return value;
                        }
                    },
                    {
                        field: 'spendtime',
                        title: '花费时间'
                    },
                    {
                        field: 'spendpay',
                        title: '花费金额'
                    },
                    {
                        field: 'onoff',
                        title: '状态',

                        formatter: function (value, row, index) {
                            var e = '<a class="btn btn-success btn-xs" href="#" mce_href="#" title="点击启用,当前状态为停用" onclick="changeonoff(\''
                                + row.id
                                + '\',\''
                                + 0
                                + '\')"><i class="fa fa-hourglass-start"></i>启用</a> ';
                            var f = '<a class="btn btn-danger btn-xs" href="#" mce_href="#" title="点击停用,当前状态为启用生效" onclick="changeonoff(\''
                                + row.id
                                + '\',\''
                                + 1
                                + '\')"><i class="fa fa-square-o">停用</i></a> ';
                            if (row.onoff == 0) {
                                return f;
                            } else {

                                return e;
                            }


                        },
                    },
                    {
                        field: 'gmtcreate',
                        title: '创建时间'
                    },
                    {
                        title: '操作',
                        field: 'id',
                        align: 'center',
                        formatter: function (value, row, index) {
                            var d = '<a class="btn btn-warning btn-sm"  href="#" mce_href="#" title="查看"  onclick="viewdetail(\''
                                + row.id
                                + '\')"><i class="fa fa-code"></i></a> ';

                            var e = '<a class="btn btn-primary btn-sm ' + s_edit_h + '" href="#" mce_href="#" title="编辑" onclick="edit(\''
                                + row.id
                                + '\')"><i class="fa fa-edit"></i></a> ';
                            // var d = '<a class="btn btn-warning btn-sm ' + s_remove_h + '" href="#" title="删除"  mce_href="#" onclick="remove(\''
                            //     + row.id
                            //     + '\')"><i class="fa fa-remove"></i></a> ';

                            // return e + d+g;
                            return e + d;
                        }
                    }]
            });
}

function changeonoff(id, onoff) {
    var actCh;
    var cmd;
    if (onoff == 0) {
        onoff = '0';
        actCh = "确认要启用该条目吗？";
    } else {
        onoff = '1';
        actCh = "确认要停用条目吗？谨慎!!!";
    }
    layer.confirm(actCh, {
        btn: ['确定', '取消']
    }, function () {
        $.ajax({
            url: prefix + "/changeonOff",
            type: "post",
            data: {
                'id': id,
                'onoff': onoff
            },
            success: function (r) {
                if (r.code == 0) {
                    layer.msg(r.msg);
                    reLoad();
                } else {
                    layer.msg(r.msg);
                }
            }
        });
    })
}

function reLoad() {
    $('#exampleTable').bootstrapTable('refresh');
}

function add() {
    layer.open({
        type: 2,
        title: '增加',
        maxmin: true,
        shadeClose: false, // 点击遮罩关闭层
        area: ['800px', '520px'],
        content: prefix + '/add' // iframe的url
    });
}

function edit(id) {
    layer.open({
        type: 2,
        title: '编辑',
        maxmin: true,
        shadeClose: false, // 点击遮罩关闭层
        area: ['800px', '520px'],
        content: prefix + '/edit/' + id // iframe的url
    });
}

function remove(id) {
    layer.confirm('确定要删除选中的记录？', {
        btn: ['确定', '取消']
    }, function () {
        $.ajax({
            url: prefix + "/remove",
            type: "post",
            data: {
                'id': id
            },
            success: function (r) {
                if (r.code == 0) {
                    layer.msg(r.msg);
                    reLoad();
                } else {
                    layer.msg(r.msg);
                }
            }
        });
    })
}

function resetPwd(id) {
}

function batchRemove() {
    var rows = $('#exampleTable').bootstrapTable('getSelections'); // 返回所有选择的行，当没有选择的记录时，返回一个空数组
    if (rows.length == 0) {
        layer.msg("请选择要删除的数据");
        return;
    }
    layer.confirm("确认要删除选中的'" + rows.length + "'条数据吗?", {
        btn: ['确定', '取消']
        // 按钮
    }, function () {
        var ids = new Array();
        // 遍历所有选择的行数据，取每条数据对应的ID
        $.each(rows, function (i, row) {
            ids[i] = row['id'];
        });
        $.ajax({
            type: 'POST',
            data: {
                "ids": ids
            },
            url: prefix + '/batchRemove',
            success: function (r) {
                if (r.code == 0) {
                    layer.msg(r.msg);
                    reLoad();
                } else {
                    layer.msg(r.msg);
                }
            }
        });
    }, function () {

    });
}