package com.ustinian.cheeseaicode.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ustinian.cheeseaicode.model.entity.App;
import com.ustinian.cheeseaicode.mapper.AppMapper;
import com.ustinian.cheeseaicode.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/Ustinian-zh">Ustinian-zh</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
