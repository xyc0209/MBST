package com.mbs.common.bean;

import com.mbs.common.base.MUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MAllUserBean {
    private List<MUser> allUserList;

    public MAllUserBean(List<MUser> allUserList) {
        this.allUserList = allUserList;
    }

    public MAllUserBean() {

    }
}
