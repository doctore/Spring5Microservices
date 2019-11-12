package com.security.oauth.configuration.security.oauth;

import com.security.oauth.configuration.Constants;
import com.security.oauth.service.cache.ClientDetailsCacheService;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

/**
 * Manages the Oauth client information, including how to insert, update, etc new ones.
 */
public class CustomJdbcClientDetailsService extends JdbcClientDetailsService {

    private static final String OAUTH_CLIENT_TABLE = Constants.DATABASE_SCHEMA.SECURITY + ".oauth_client_details";
    private static final String CLIENT_FIELDS_FOR_UPDATE = "resource_ids, scope, authorized_grant_types, "
                                                         + "web_server_redirect_uri, authorities, access_token_validity, "
                                                         + "refresh_token_validity, additional_information, autoapprove";

    private static final String CLIENT_FIELDS = "client_secret, " + CLIENT_FIELDS_FOR_UPDATE;
    private static final String BASE_FIND_STATEMENT = "select client_id, " + CLIENT_FIELDS + " from " + OAUTH_CLIENT_TABLE;
    private static final String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";
    private static final String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";

    private static final String DEFAULT_INSERT_STATEMENT = "insert into " + OAUTH_CLIENT_TABLE + " (" + CLIENT_FIELDS
                                                         + ", client_id) values (?,?,?,?,?,?,?,?,?,?,?)";

    private static final String DEFAULT_UPDATE_STATEMENT = "update " + OAUTH_CLIENT_TABLE + " set "
                                                          + CLIENT_FIELDS_FOR_UPDATE.replaceAll(", ", "=?, ")
                                                          + "=? where client_id = ?";

    private static final String DEFAULT_UPDATE_SECRET_STATEMENT = "update " + OAUTH_CLIENT_TABLE + " "
                                                                + "set client_secret = ? "
                                                                + "where client_id = ?";

    private static final String DEFAULT_DELETE_STATEMENT = "delete from " + OAUTH_CLIENT_TABLE + " where client_id = ?";

    private ClientDetailsCacheService clientDetailsCacheService;


    public CustomJdbcClientDetailsService(DataSource dataSource, ClientDetailsCacheService clientDetailsCacheService) {
        super(dataSource);
        this.clientDetailsCacheService = clientDetailsCacheService;
        initSQL();
    }


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        return clientDetailsCacheService.get(clientId)
                .orElseGet(() -> {
                    ClientDetails clientDetails = super.loadClientByClientId(clientId);
                    clientDetailsCacheService.put(clientId, clientDetails);
                    return clientDetails;
                });
    }

    /**
     * Add the required SQL commands for every database operation.
     */
    private void initSQL() {
        this.setDeleteClientDetailsSql(DEFAULT_DELETE_STATEMENT);
        this.setFindClientDetailsSql(DEFAULT_FIND_STATEMENT);
        this.setInsertClientDetailsSql(DEFAULT_INSERT_STATEMENT);
        this.setSelectClientDetailsSql(DEFAULT_SELECT_STATEMENT);
        this.setUpdateClientDetailsSql(DEFAULT_UPDATE_STATEMENT);
        this.setUpdateClientSecretSql(DEFAULT_UPDATE_SECRET_STATEMENT);
    }

}
