package com.xylanny.backend.controller;

import com.xylanny.backend.model.dto.ChartAnalysisDTO;
import com.xylanny.backend.model.dto.ChartAnalysisVO;
import com.xylanny.backend.model.dto.Result;
import com.xylanny.backend.service.ChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chart")
public class ChartController {

    private final ChartService chartService;

    @PostMapping(value = "/analysis/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ChartAnalysisVO> generateConclusionByAi(
            @ModelAttribute ChartAnalysisDTO chartAnalysisDTO
    ) {
        log.info("开始图表分析，图表类型: {}, X轴: {}, Y轴: {}",
                chartAnalysisDTO.getChartType(),
                chartAnalysisDTO.getX(),
                chartAnalysisDTO.getY());

        ChartAnalysisVO result = chartService.analyzeChartByAi(chartAnalysisDTO);

        log.info("图表分析完成");
        return Result.success(result);
    }
}
