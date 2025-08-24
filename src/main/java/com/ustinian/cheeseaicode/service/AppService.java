package com.ustinian.cheeseaicode.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ustinian.cheeseaicode.model.dto.app.AppQueryRequest;
import com.ustinian.cheeseaicode.model.entity.App;
import com.ustinian.cheeseaicode.model.entity.User;
import com.ustinian.cheeseaicode.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/Ustinian-zh">Ustinian-zh</a>
 */
public interface AppService extends IService<App> {
    /**
     * 获取 AppVO对象
     * @param app app 对象
     * @return AppVO对象
     */
    AppVO getAppVO(App app);

    /**
     * 构造查询单个对象的方法
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取AppVO列表对象的方法
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * chatToGenCode 方‌法，调用门面生成代‍码,前后端交互唯一
     * @param appId
     * @param message
     * @param loginUser
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 网站应用部署
     * @param appId
     * @param loginUser
     * @return
     */
    String deployApp(Long appId, User loginUser);
}
