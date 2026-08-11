package edu.stanford.bmir.protege.web.server.integration.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.integration.dispatch.ActionDispatch;
import edu.stanford.bmir.protege.web.server.integration.dto.IndividualRuntimeData;
import edu.stanford.bmir.protege.web.server.integration.dto.IndividualRuntimeDataRequest;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.dispatch.UpdateObjectResult;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetNamedIndividualFrameAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetNamedIndividualFrameResult;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UpdateNamedIndividualFrameAction;
import edu.stanford.bmir.protege.web.shared.entity.OWLDataPropertyData;
import edu.stanford.bmir.protege.web.shared.entity.OWLLiteralData;
import edu.stanford.bmir.protege.web.shared.entity.OWLNamedIndividualData;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.frame.NamedIndividualFrame;
import edu.stanford.bmir.protege.web.shared.frame.PropertyLiteralValue;
import edu.stanford.bmir.protege.web.shared.frame.State;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IndividualRuntimeDataService_TestCase {

    private static final String PROJECT_ID = "00000000-0000-0000-0000-000000000001";
    private static final String INDIVIDUAL_IRI = "http://example.org/Sensor1";
    private static final String STATUS_PROPERTY = "http://example.org/hasStatus";

    @Mock
    private ActionDispatch actionDispatch;

    private IndividualRuntimeDataService service;

    private UserId userId;

    @Before
    public void setUp() {
        service = new IndividualRuntimeDataService(actionDispatch);
        userId = UserId.getUserId("api-user");
    }

    @Test
    public void shouldGetRuntimeDataFromOntologyFrame() {
        NamedIndividualFrame frame = frameWithLiteral(STATUS_PROPERTY, "RUNNING");
        when(actionDispatch.execute(any(GetNamedIndividualFrameAction.class), eq(userId)))
                .thenReturn(new GetNamedIndividualFrameResult(frame));

        IndividualRuntimeData data = service.getRuntimeData(PROJECT_ID, INDIVIDUAL_IRI, userId);

        assertThat(data.getProjectId(), is(PROJECT_ID));
        assertThat(data.getIndividualIri(), is(INDIVIDUAL_IRI));
        assertThat(data.getProperties(), hasEntry(STATUS_PROPERTY, "RUNNING"));
    }

    @Test
    public void shouldPutRuntimeDataViaUpdateFrameAction() {
        NamedIndividualFrame frame = frameWithLiteral(STATUS_PROPERTY, "IDLE");
        when(actionDispatch.execute(any(GetNamedIndividualFrameAction.class), eq(userId)))
                .thenReturn(new GetNamedIndividualFrameResult(frame));
        when(actionDispatch.execute(any(UpdateNamedIndividualFrameAction.class), eq(userId)))
                .thenReturn(new UpdateObjectResult(new EventList<ProjectEvent<?>>(EventTag.get(1), EventTag.get(1))));

        IndividualRuntimeDataRequest request = new IndividualRuntimeDataRequest(
                INDIVIDUAL_IRI,
                Collections.singletonMap(STATUS_PROPERTY, "RUNNING")
        );

        IndividualRuntimeData saved = service.putRuntimeData(PROJECT_ID, INDIVIDUAL_IRI, request, userId);

        assertThat(saved.getProperties(), hasEntry(STATUS_PROPERTY, "RUNNING"));
        assertThat(saved.getUpdatedBy(), is("api-user"));

        ArgumentCaptor<Object> allCaptor = ArgumentCaptor.forClass(Object.class);
        verify(actionDispatch, times(2)).execute(
                (edu.stanford.bmir.protege.web.shared.dispatch.Action) allCaptor.capture(),
                eq(userId));
        UpdateNamedIndividualFrameAction updateAction = allCaptor.getAllValues().stream()
                .filter(UpdateNamedIndividualFrameAction.class::isInstance)
                .map(UpdateNamedIndividualFrameAction.class::cast)
                .findFirst()
                .orElseThrow(AssertionError::new);
        assertThat(updateAction.getTo().getPropertyValues(), hasSize(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectBlankIndividualIri() {
        service.getRuntimeData(PROJECT_ID, "  ", userId);
    }

    private static NamedIndividualFrame frameWithLiteral(String propertyIri, String value) {
        OWLNamedIndividual individual = DataFactory.getOWLNamedIndividual(INDIVIDUAL_IRI);
        OWLNamedIndividualData subject = OWLNamedIndividualData.get(individual, ImmutableMap.of());
        return NamedIndividualFrame.get(
                subject,
                ImmutableSet.of(),
                ImmutableSet.of(
                        PropertyLiteralValue.get(
                                OWLDataPropertyData.get(DataFactory.getOWLDataProperty(propertyIri), ImmutableMap.of()),
                                OWLLiteralData.get(DataFactory.getOWLLiteral(value)),
                                State.ASSERTED)
                ),
                ImmutableSet.of()
        );
    }
}
