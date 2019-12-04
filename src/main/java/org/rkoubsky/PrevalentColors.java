package org.rkoubsky;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class PrevalentColors {
    public final String imagUrl;
    public final List<String> prevalentColors;
}
