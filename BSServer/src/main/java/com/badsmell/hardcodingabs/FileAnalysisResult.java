package com.badsmell.hardcodingabs;


import lombok.Data;

@Data
public class FileAnalysisResult {

    private String fileName;

    private String filePath;

    private String hardCode;

    private Integer place;  //the row position of the hardCode in the file

}
