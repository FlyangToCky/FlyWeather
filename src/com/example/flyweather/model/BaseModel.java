package com.example.flyweather.model;

import com.litesuits.orm.db.annotation.Check;
import com.litesuits.orm.db.annotation.Ignore;

import java.io.Serializable;

/**
 * @author MaTianyu
 * @date 14-7-22
 */
public class BaseModel implements Serializable {

    @Check("bm NOT NULL")
    public String bm     = "ȫ����";

    @Ignore
    private String ignore = " ������Բ�����������ݿ���ġ���Ϊ�������ignore";

    @Override
    public String toString() {
        return "BaseModel{" +
                "bm='" + bm + '\'' +
                '}';
    }
}
