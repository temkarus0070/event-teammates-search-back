package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class ThemeTagKey implements Serializable {

    private Long themeId;

    private Long tagId;
}
