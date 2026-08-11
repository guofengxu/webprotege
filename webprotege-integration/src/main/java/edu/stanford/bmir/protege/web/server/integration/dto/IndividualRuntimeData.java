package edu.stanford.bmir.protege.web.server.integration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Runtime data associated with a named individual in a project.
 * Intended for third-party system integration (sensor values, device state, etc.).
 */
public class IndividualRuntimeData {

    @Nonnull
    private final String projectId;

    @Nonnull
    private final String individualIri;

    @Nonnull
    private final Map<String, String> properties;

    private final long updatedAt;

    @Nullable
    private final String updatedBy;

    @JsonCreator
    public IndividualRuntimeData(@JsonProperty("projectId") @Nonnull String projectId,
                                 @JsonProperty("individualIri") @Nonnull String individualIri,
                                 @JsonProperty("properties") @Nullable Map<String, String> properties,
                                 @JsonProperty("updatedAt") long updatedAt,
                                 @JsonProperty("updatedBy") @Nullable String updatedBy) {
        this.projectId = Objects.requireNonNull(projectId, "projectId");
        this.individualIri = Objects.requireNonNull(individualIri, "individualIri");
        this.properties = properties == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(properties));
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    @Nonnull
    @JsonProperty("projectId")
    public String getProjectId() {
        return projectId;
    }

    @Nonnull
    @JsonProperty("individualIri")
    public String getIndividualIri() {
        return individualIri;
    }

    @Nonnull
    @JsonProperty("properties")
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonProperty("updatedAt")
    public long getUpdatedAt() {
        return updatedAt;
    }

    @Nullable
    @JsonProperty("updatedBy")
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Nonnull
    public IndividualRuntimeData withProperties(@Nonnull Map<String, String> newProperties,
                                                long newUpdatedAt,
                                                @Nullable String newUpdatedBy) {
        return new IndividualRuntimeData(projectId, individualIri, newProperties, newUpdatedAt, newUpdatedBy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IndividualRuntimeData)) {
            return false;
        }
        IndividualRuntimeData that = (IndividualRuntimeData) o;
        return updatedAt == that.updatedAt
                && projectId.equals(that.projectId)
                && individualIri.equals(that.individualIri)
                && properties.equals(that.properties)
                && Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, individualIri, properties, updatedAt, updatedBy);
    }

    @Override
    public String toString() {
        return "IndividualRuntimeData{" +
                "projectId='" + projectId + '\'' +
                ", individualIri='" + individualIri + '\'' +
                ", properties=" + properties +
                ", updatedAt=" + updatedAt +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
