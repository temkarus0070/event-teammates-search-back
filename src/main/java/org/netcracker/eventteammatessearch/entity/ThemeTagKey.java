package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class ThemeTagKey implements Serializable {

    private Long themeId;

    private Long tagId;
}
