package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinlin.yupicturebackend.mapper.UserMapper;
import com.jinlin.yupicturebackend.model.entity.User;
import generator.service.PictureService;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-09-27 12:15:42
*/
@Service
public class PictureServiceImpl extends ServiceImpl<UserMapper.PictureMapper, User.Picture>
    implements PictureService{

}




