package com.troy.camunda.domian.VO;

import java.io.Serializable;

public class CamundaCommentVO implements Serializable {

    private String type;

    /**
     * 意见内容
     */
    private String comment;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
