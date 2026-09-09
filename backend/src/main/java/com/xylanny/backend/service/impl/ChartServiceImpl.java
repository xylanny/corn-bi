package com.xylanny.backend.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.xylanny.backend.model.entity.Chart;
import com.xylanny.backend.service.ChartService;
import com.xylanny.backend.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author Jun
* @description 针对表【chart(图表表)】的数据库操作Service实现
* @createDate 2026-09-08 10:56:19
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}


