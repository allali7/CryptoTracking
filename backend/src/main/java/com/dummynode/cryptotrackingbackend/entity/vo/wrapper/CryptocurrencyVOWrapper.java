package com.dummynode.cryptotrackingbackend.entity.vo.wrapper;

import com.dummynode.cryptotrackingbackend.entity.vo.CryptocurrencyVO;
import com.dummynode.cryptotrackingbackend.tool.Util;
import lombok.Data;

@Data
public class CryptocurrencyVOWrapper {

    private CryptocurrencyVO vo;

    private String formattedMaxSupply;

    public CryptocurrencyVOWrapper(CryptocurrencyVO vo) {
        this.vo = vo;
    }

    public static CryptocurrencyVOWrapper toWrapper(CryptocurrencyVO vo) {
        return new CryptocurrencyVOWrapper(vo);
    }

}