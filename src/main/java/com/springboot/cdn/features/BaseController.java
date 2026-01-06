package com.springboot.cdn.features;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("CdnBaseController")
@RequestMapping(value = "/app/cdn", produces = "application/json")
public class BaseController {
}
