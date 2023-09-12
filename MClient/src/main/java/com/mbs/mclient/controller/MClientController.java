package com.mbs.mclient.controller;


import com.mbs.mclient.core.MClientSkeleton;
import com.mbs.common.bean.MApiContinueRequest;
import com.mbs.common.bean.MApiSplitBean;
import com.mbs.common.bean.MClientInfoBean;
import com.mbs.common.bean.MInstanceRestInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.util.HashSet;
import java.util.List;


@RestController
@EnableAutoConfiguration
@RequestMapping("/mclient")
public class MClientController {


    @ResponseBody
    @RequestMapping(path = "/getMObjectIdList", method = RequestMethod.GET)
    public List<String> getMObjectIdList() {
        return MClientSkeleton.getInstance().getMObjectIdList();
    }



    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    /**
     * Do something for MClient App:
     *   * Register new metadata so we can identify whether it is a MClient app or not
     */

    @ResponseBody
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public MClientInfoBean getInfo() {
        MClientInfoBean infoBean = new MClientInfoBean();
        infoBean.setApiMap(MClientSkeleton.getInstance().getObjectId2ApiSet());
        infoBean.setParentIdMap(MClientSkeleton.getInstance().getParentIdMap());
        infoBean.setMObjectIdSet(new HashSet<>(MClientSkeleton.getInstance().getMObjectIdList()));
        return infoBean;
    }

    @RequestMapping(path = "/setRestInfo", method = RequestMethod.POST)
    public void setRestInfo(@RequestBody MInstanceRestInfoBean restInfoBean) {
        MClientSkeleton.getInstance().addRestInfo(restInfoBean);
    }

    @RequestMapping(path = "/setApiContinueStatus", method = RequestMethod.POST)
    public void setApiContinueStatus(@RequestBody MApiContinueRequest continueStatus) {
        for (MApiSplitBean splitBean : continueStatus.getSplitBeans()) {
            MClientSkeleton.getInstance().setApiContinueStatus(splitBean);
        }
    }

    @ResponseBody
    @RequestMapping(path = "/getRestInfoList", method = RequestMethod.GET)
    public List<MInstanceRestInfoBean> getRestInfoList() {
        return MClientSkeleton.getInstance().getRestInfoBeanList();
    }
}
