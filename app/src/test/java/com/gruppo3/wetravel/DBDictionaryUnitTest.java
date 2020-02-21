package com.gruppo3.wetravel;

import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

public class DBDictionaryUnitTest  {

    @Test
    public void LatLngToStringAndBackEquivalence(){
        LatLng toTest=new LatLng(70.6543,68.987654);

        String transformed=DBDictionary.convertLatLngToString(toTest);

        LatLng retrieved=DBDictionary.convertStringToLatLng(transformed);

        Assert.assertEquals(toTest,retrieved);
    }
}
