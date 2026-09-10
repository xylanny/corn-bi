package com.xylanny.backend.controller;

import com.xylanny.backend.exception.BusinessException;
import com.xylanny.backend.model.dto.KMeansDTO;
import com.xylanny.backend.model.dto.KMeansVO;
import com.xylanny.backend.model.dto.Result;
import com.xylanny.backend.model.enums.BusinessCode;
import com.xylanny.backend.service.TableService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("table")
@RequiredArgsConstructor
@Slf4j
public class TableController {

    private final TableService tableService;

    @PostMapping(value = "/analysis/kmeans", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<KMeansVO> analysisByKmeans(
            @ModelAttribute KMeansDTO kMeansDTO,
            HttpServletRequest request
    ) {

        try {
            // 校验参数
            validateRequest(kMeansDTO);

            String authorization = request.getHeader("Authorization");
            return Result.success(tableService.analysisByKmeans(kMeansDTO, authorization));
        } catch (IllegalArgumentException e) {
            throw new BusinessException(BusinessCode.PARAMS_ERROR, e.getMessage());
        } catch (IOException e) {
            log.error("数据集文件读取失败", e);
            throw new BusinessException(BusinessCode.PARAMS_ERROR, "数据集文件读出失败");
        }
    }

    private void validateRequest(KMeansDTO kMeansDTO) {

        if (kMeansDTO == null || kMeansDTO.getFile() == null || kMeansDTO.getFile().isEmpty()) {
            throw new BusinessException(BusinessCode.PARAMS_ERROR, "数据集文件不能为空");
        }

        if (kMeansDTO.getK() == null || kMeansDTO.getK() < 2) {
            throw new BusinessException(BusinessCode.PARAMS_ERROR, "聚类数量必须大于等于 2");
        }

        if (kMeansDTO.getMaxIterations() != null && kMeansDTO.getMaxIterations() < 1) {
            throw new BusinessException(BusinessCode.PARAMS_ERROR, "最大迭代数必须大于 0");
        }

        if (kMeansDTO.getTolerance() != null
                && (!Double.isFinite(kMeansDTO.getTolerance()) || kMeansDTO.getTolerance() <= 0)) {
            throw new BusinessException(BusinessCode.PARAMS_ERROR, "收敛阈值必须大于 0");
        }
    }
}
