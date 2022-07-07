package org.netcracker.eventteammatessearch.Projections;

import java.util.List;

public interface UserProjection {
public List<String> getAuthorities();
public String getLogin();
public String getFirstName();
public String getLastName();
public String getEmail();
public String getPhone();
public String getPictureUrl();
public boolean isOauthUser();
public boolean isPhoneConfirmed();
public boolean isCommercialUser();
public boolean isCommercialUserCreated();
public String getOrganizationName();
public String getDescription();
}
