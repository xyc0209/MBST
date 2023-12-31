package com.mbs.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MBuildInfoRequest {
    private String projectName;
    private String moduleName;
    private String gitUrl;
    private String gitTag;
    private String imageName;
    private String imageTag;
    private String imageOwner;
    private String id;
    private String branch;
}
