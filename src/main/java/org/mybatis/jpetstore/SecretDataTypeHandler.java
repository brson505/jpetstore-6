/*
 *    Copyright 2010-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Encryption and decryption TypeHandler
 */
@MappedJdbcTypes(value = JdbcType.VARCHAR)
@MappedTypes(SecretData.class)
public class SecretDataTypeHandler extends BaseTypeHandler<SecretData> {

  private static final byte[] KEYS = "12345678abcdefgh".getBytes(StandardCharsets.UTF_8);

  /**
   * Set parameters
   */
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, SecretData parameter, JdbcType jdbcType)
      throws SQLException {
    if (parameter == null || parameter.getValue() == null) {
      ps.setString(i, null);
      return;
    }
    ps.setString(i, encrypt(parameter));
  }

  /**
   * Get value
   */
  @Override
  public SecretData getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return decrypt(rs.getString(columnName));
  }

  /**
   * Get value
   */
  @Override
  public SecretData getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return decrypt(rs.getString(columnIndex));
  }

  /**
   * Get value
   */
  @Override
  public SecretData getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return decrypt(cs.getString(columnIndex));
  }

  public SecretData decrypt(String value) {
    System.out.println("decrypt " + value);
    if (null == value) {
      return null;
    }
    return new SecretData(new String(Base64.decodeBase64(value), StandardCharsets.UTF_8));
  }

  private String encrypt(SecretData secretData) {
    System.out.println("encrypt " + secretData.getValue());
    if (secretData == null || secretData.getValue() == null) {
      return null;
    }

    return Base64.encodeBase64String(secretData.getValue().getBytes(StandardCharsets.UTF_8));
  }
}
