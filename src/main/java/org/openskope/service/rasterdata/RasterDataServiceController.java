package org.openskope.service.locationinfo;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.File;

import org.openskope.util.ProcessRunner;
import org.openskope.util.StreamSink;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@EnableAutoConfiguration 
@CrossOrigin 
@RequestMapping("/${rasterdata-service.prefix}/api/${rasterdata-service.version}/")
public class RasterDataServiceController {
    
    private final static String files[];
    
    static {
        files = new String[] {
            "GDD_may_sept_demosaic.tif",
            "PPT_annual_demosaic.tif",
            "PPT_may_sept_demosaic.tif",
            "PPT_water_year.tif"
        };
    };

	@Value("${rasterdata-service.data-dir}") public String dataDirectory;

	@RequestMapping(value="timeseries", method=RequestMethod.GET)
    @ResponseBody
	public ConditionVsTimeResponse getDetail(
            @RequestParam(value="long", required=true) String longitude,
            @RequestParam(value="lat", required=true) String latitude
        ) throws Exception {

        ConditionVsTimeResponse response = new ConditionVsTimeResponse();

        for (String fileName : files) {
            String commandLine = String.format(
                "gdallocationinfo -valonly -wgs84 %s %s %s", dataDirectory + "/" + fileName, longitude, latitude);
            System.out.println(commandLine);
            StreamSink streams[] = ProcessRunner.run(commandLine, "", new String[0], null);
            response.put(fileName, streams[0].toString().split("\\s+"));
        }

		return response;
	}
}
