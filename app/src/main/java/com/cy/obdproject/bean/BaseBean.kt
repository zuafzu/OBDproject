package com.cy.obdproject.bean

/**
 * Created by Administrator on 2017/10/30 0030.
 */
class BaseBean {

    var code: String? = null
    var msg: String? = null
    var data: String? = null

    override fun toString(): String {
        return "BaseBean{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}'
    }
}