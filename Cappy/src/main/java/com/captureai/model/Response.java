/*package com.captureai.model;


import lombok.Data;
import java.util.Map;


@Data
public class Response {
    private Map<String, String> captions;
    private String raw;
}*/

package com.captureai.model;

import lombok.Data;

import java.util.Map;

@Data
public class Response {
    private Map<String, Object> captions;  // parsed captions if available
    private String raw;                    // raw response text

}

