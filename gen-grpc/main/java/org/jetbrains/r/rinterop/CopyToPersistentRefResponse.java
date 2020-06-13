// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package org.jetbrains.r.rinterop;

/**
 * Protobuf type {@code rplugininterop.CopyToPersistentRefResponse}
 */
public  final class CopyToPersistentRefResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:rplugininterop.CopyToPersistentRefResponse)
    CopyToPersistentRefResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use CopyToPersistentRefResponse.newBuilder() to construct.
  private CopyToPersistentRefResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private CopyToPersistentRefResponse() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new CopyToPersistentRefResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private CopyToPersistentRefResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {
            responseCase_ = 1;
            response_ = input.readInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();
            responseCase_ = 2;
            response_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_CopyToPersistentRefResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_CopyToPersistentRefResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.jetbrains.r.rinterop.CopyToPersistentRefResponse.class, org.jetbrains.r.rinterop.CopyToPersistentRefResponse.Builder.class);
  }

  private int responseCase_ = 0;
  private java.lang.Object response_;
  public enum ResponseCase
      implements com.google.protobuf.Internal.EnumLite {
    PERSISTENTINDEX(1),
    ERROR(2),
    RESPONSE_NOT_SET(0);
    private final int value;
    private ResponseCase(int value) {
      this.value = value;
    }
    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static ResponseCase valueOf(int value) {
      return forNumber(value);
    }

    public static ResponseCase forNumber(int value) {
      switch (value) {
        case 1: return PERSISTENTINDEX;
        case 2: return ERROR;
        case 0: return RESPONSE_NOT_SET;
        default: return null;
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public ResponseCase
  getResponseCase() {
    return ResponseCase.forNumber(
        responseCase_);
  }

  public static final int PERSISTENTINDEX_FIELD_NUMBER = 1;
  /**
   * <code>int32 persistentIndex = 1;</code>
   */
  public int getPersistentIndex() {
    if (responseCase_ == 1) {
      return (java.lang.Integer) response_;
    }
    return 0;
  }

  public static final int ERROR_FIELD_NUMBER = 2;
  /**
   * <code>string error = 2;</code>
   */
  public java.lang.String getError() {
    java.lang.Object ref = "";
    if (responseCase_ == 2) {
      ref = response_;
    }
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      if (responseCase_ == 2) {
        response_ = s;
      }
      return s;
    }
  }
  /**
   * <code>string error = 2;</code>
   */
  public com.google.protobuf.ByteString
      getErrorBytes() {
    java.lang.Object ref = "";
    if (responseCase_ == 2) {
      ref = response_;
    }
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      if (responseCase_ == 2) {
        response_ = b;
      }
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (responseCase_ == 1) {
      output.writeInt32(
          1, (int)((java.lang.Integer) response_));
    }
    if (responseCase_ == 2) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, response_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (responseCase_ == 1) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(
            1, (int)((java.lang.Integer) response_));
    }
    if (responseCase_ == 2) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, response_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.jetbrains.r.rinterop.CopyToPersistentRefResponse)) {
      return super.equals(obj);
    }
    org.jetbrains.r.rinterop.CopyToPersistentRefResponse other = (org.jetbrains.r.rinterop.CopyToPersistentRefResponse) obj;

    if (!getResponseCase().equals(other.getResponseCase())) return false;
    switch (responseCase_) {
      case 1:
        if (getPersistentIndex()
            != other.getPersistentIndex()) return false;
        break;
      case 2:
        if (!getError()
            .equals(other.getError())) return false;
        break;
      case 0:
      default:
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    switch (responseCase_) {
      case 1:
        hash = (37 * hash) + PERSISTENTINDEX_FIELD_NUMBER;
        hash = (53 * hash) + getPersistentIndex();
        break;
      case 2:
        hash = (37 * hash) + ERROR_FIELD_NUMBER;
        hash = (53 * hash) + getError().hashCode();
        break;
      case 0:
      default:
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.jetbrains.r.rinterop.CopyToPersistentRefResponse prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code rplugininterop.CopyToPersistentRefResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:rplugininterop.CopyToPersistentRefResponse)
      org.jetbrains.r.rinterop.CopyToPersistentRefResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_CopyToPersistentRefResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_CopyToPersistentRefResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.jetbrains.r.rinterop.CopyToPersistentRefResponse.class, org.jetbrains.r.rinterop.CopyToPersistentRefResponse.Builder.class);
    }

    // Construct using org.jetbrains.r.rinterop.CopyToPersistentRefResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      responseCase_ = 0;
      response_ = null;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_CopyToPersistentRefResponse_descriptor;
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.CopyToPersistentRefResponse getDefaultInstanceForType() {
      return org.jetbrains.r.rinterop.CopyToPersistentRefResponse.getDefaultInstance();
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.CopyToPersistentRefResponse build() {
      org.jetbrains.r.rinterop.CopyToPersistentRefResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.CopyToPersistentRefResponse buildPartial() {
      org.jetbrains.r.rinterop.CopyToPersistentRefResponse result = new org.jetbrains.r.rinterop.CopyToPersistentRefResponse(this);
      if (responseCase_ == 1) {
        result.response_ = response_;
      }
      if (responseCase_ == 2) {
        result.response_ = response_;
      }
      result.responseCase_ = responseCase_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.jetbrains.r.rinterop.CopyToPersistentRefResponse) {
        return mergeFrom((org.jetbrains.r.rinterop.CopyToPersistentRefResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.jetbrains.r.rinterop.CopyToPersistentRefResponse other) {
      if (other == org.jetbrains.r.rinterop.CopyToPersistentRefResponse.getDefaultInstance()) return this;
      switch (other.getResponseCase()) {
        case PERSISTENTINDEX: {
          setPersistentIndex(other.getPersistentIndex());
          break;
        }
        case ERROR: {
          responseCase_ = 2;
          response_ = other.response_;
          onChanged();
          break;
        }
        case RESPONSE_NOT_SET: {
          break;
        }
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.jetbrains.r.rinterop.CopyToPersistentRefResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.jetbrains.r.rinterop.CopyToPersistentRefResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int responseCase_ = 0;
    private java.lang.Object response_;
    public ResponseCase
        getResponseCase() {
      return ResponseCase.forNumber(
          responseCase_);
    }

    public Builder clearResponse() {
      responseCase_ = 0;
      response_ = null;
      onChanged();
      return this;
    }


    /**
     * <code>int32 persistentIndex = 1;</code>
     */
    public int getPersistentIndex() {
      if (responseCase_ == 1) {
        return (java.lang.Integer) response_;
      }
      return 0;
    }
    /**
     * <code>int32 persistentIndex = 1;</code>
     */
    public Builder setPersistentIndex(int value) {
      responseCase_ = 1;
      response_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 persistentIndex = 1;</code>
     */
    public Builder clearPersistentIndex() {
      if (responseCase_ == 1) {
        responseCase_ = 0;
        response_ = null;
        onChanged();
      }
      return this;
    }

    /**
     * <code>string error = 2;</code>
     */
    public java.lang.String getError() {
      java.lang.Object ref = "";
      if (responseCase_ == 2) {
        ref = response_;
      }
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (responseCase_ == 2) {
          response_ = s;
        }
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string error = 2;</code>
     */
    public com.google.protobuf.ByteString
        getErrorBytes() {
      java.lang.Object ref = "";
      if (responseCase_ == 2) {
        ref = response_;
      }
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        if (responseCase_ == 2) {
          response_ = b;
        }
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string error = 2;</code>
     */
    public Builder setError(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  responseCase_ = 2;
      response_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string error = 2;</code>
     */
    public Builder clearError() {
      if (responseCase_ == 2) {
        responseCase_ = 0;
        response_ = null;
        onChanged();
      }
      return this;
    }
    /**
     * <code>string error = 2;</code>
     */
    public Builder setErrorBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      responseCase_ = 2;
      response_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:rplugininterop.CopyToPersistentRefResponse)
  }

  // @@protoc_insertion_point(class_scope:rplugininterop.CopyToPersistentRefResponse)
  private static final org.jetbrains.r.rinterop.CopyToPersistentRefResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.jetbrains.r.rinterop.CopyToPersistentRefResponse();
  }

  public static org.jetbrains.r.rinterop.CopyToPersistentRefResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CopyToPersistentRefResponse>
      PARSER = new com.google.protobuf.AbstractParser<CopyToPersistentRefResponse>() {
    @java.lang.Override
    public CopyToPersistentRefResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new CopyToPersistentRefResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<CopyToPersistentRefResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CopyToPersistentRefResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.jetbrains.r.rinterop.CopyToPersistentRefResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
