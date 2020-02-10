package com.gruppo3.wetravel;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

public class HelperTest {
    @Test
    public void forwardAndBack(){
        LatLng toTest=new LatLng(123.2345678,2345678.34);

        String transformed=Helper.convertLatLngToString(toTest);

        LatLng retrieved=Helper.convertStringToLatLng(transformed);

        Assert.assertEquals(toTest,retrieved);
    }
}
