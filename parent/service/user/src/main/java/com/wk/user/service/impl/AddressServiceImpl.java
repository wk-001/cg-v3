package com.wk.user.service.impl;

import com.wk.user.entity.Address;
import com.wk.user.mapper.AddressMapper;
import com.wk.user.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

}
