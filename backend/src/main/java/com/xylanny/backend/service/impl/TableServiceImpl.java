package com.xylanny.backend.service.impl;

import com.xylanny.backend.model.dto.KMeansDTO;
import com.xylanny.backend.model.dto.KMeansVO;
import com.xylanny.backend.service.TableService;
import com.xylanny.backend.service.UserService;
import com.xylanny.backend.utils.KMeansUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final UserService userService;

    @Override
    public KMeansVO analysisByKmeans(KMeansDTO kMeansDTO, String authorization) throws IOException {

        return KMeansUtil.analyze(
                kMeansDTO.getFile(),
                kMeansDTO.getColumns(),
                kMeansDTO.getK(),
                kMeansDTO.getMaxIterations(),
                kMeansDTO.getTolerance(),
                kMeansDTO.getSeed()
        );
    }
}
