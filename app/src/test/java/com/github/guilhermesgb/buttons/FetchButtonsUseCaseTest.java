package com.github.guilhermesgb.buttons;

import android.content.Context;

import com.github.guilhermesgb.buttons.model.Button;
import com.github.guilhermesgb.buttons.model.ButtonsViewState;
import com.github.guilhermesgb.buttons.model.FetchButtonsUseCase;
import com.github.guilhermesgb.buttons.model.dagger.DaggerDependencies;
import com.github.guilhermesgb.buttons.model.dagger.DatabaseModule;
import com.github.guilhermesgb.buttons.model.dagger.NetworkModule;
import com.github.guilhermesgb.buttons.model.database.ButtonDao;
import com.github.guilhermesgb.buttons.model.database.DatabaseResource;
import com.github.guilhermesgb.buttons.model.network.ApiEndpoints;
import com.github.guilhermesgb.buttons.utils.MockedServerUnitTest;
import com.github.guilhermesgb.buttons.view.FetchButtonsAction;

import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Single;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.http.GET;

import static com.github.guilhermesgb.buttons.model.Button.ButtonType.TO_BOTTOM;
import static com.github.guilhermesgb.buttons.model.Button.ButtonType.TO_LEFT;
import static com.github.guilhermesgb.buttons.model.Button.ButtonType.TO_RIGHT;
import static com.github.guilhermesgb.buttons.model.ButtonsViewState.StateType.LOADING;
import static com.github.guilhermesgb.buttons.model.ButtonsViewState.StateType.LOADING_FAILURE;
import static com.github.guilhermesgb.buttons.model.ButtonsViewState.StateType.LOADING_SUCCESS;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FetchButtonsUseCaseTest extends MockedServerUnitTest {

    private FetchButtonsUseCase setupFetchButtonsUseCase(String baseUrl) {
        return spy(DaggerDependencies.builder()
            .databaseModule(new DatabaseModule(mock(Context.class)))
            .networkModule(new NetworkModule(baseUrl))
            .build().fetchButtonsUseCase());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void fetchButtons_withNoLocalButtons_andNoRemoteButtonsAvailable_shouldYieldEmptyResults() throws Exception {
        // ### SETUP PHASE ###

        //Setting up mock server to return empty list of buttons.
        List<MockResponse> expectedResponses = Collections.singletonList
            (new MockResponse().setResponseCode(200).setBody("[]"));
        configureMockWebServer(expectedResponses, (server, baseUrl) -> {
            FetchButtonsUseCase fetchButtonsUseCase = setupFetchButtonsUseCase(baseUrl);

            ButtonDao buttonDaoMock = mock(ButtonDao.class);
            //Mocking database to return empty list of buttons as well.
            when(buttonDaoMock.findAll()).thenReturn(Single.just(new LinkedList<>()));
            //Turning database writes into no-ops.
            doNothing().when(buttonDaoMock).insertAll(ArgumentMatchers.anyList());
            doNothing().when(buttonDaoMock).deleteAll();
            DatabaseResource databaseMock = mock(DatabaseResource.class);
            when(databaseMock.buttonDao()).thenReturn(buttonDaoMock);
            doReturn(databaseMock).when(fetchButtonsUseCase).getDatabase();

            // ### EXECUTION PHASE ###

            final List<ButtonsViewState> states = new LinkedList<>();
            fetchButtonsUseCase.fetchButtons(new FetchButtonsAction())
                .blockingIterable().forEach(states::add);

            // ### VERIFICATION PHASE ###

            assertThat(states, hasSize(3));
            assertThat(states.get(0), hasProperty("type", equalTo(LOADING)));
            assertThat(states.get(1), hasProperty("type", equalTo(LOADING_SUCCESS))); //local
            assertThat(states.get(2), hasProperty("type", equalTo(LOADING_SUCCESS))); //remote

            assertThat(states.get(1).getButtons(), hasSize(0));

            assertThat(states.get(2).getButtons(), hasSize(0));

            //Verifying if test made expected API calls.
            assertThat(server.getRequestCount(), is(1));
            String expectedEndpoint = "/" + ApiEndpoints.class
                .getMethod("getButtons").getAnnotation(GET.class).value();
            RecordedRequest request = server.takeRequest();
            assertThat(request.getPath(), is(expectedEndpoint));
            server.shutdown();

            //Verifying if test made expected database operations.
            verify(buttonDaoMock).findAll();
            verify(buttonDaoMock).deleteAll();
            verify(buttonDaoMock).insertAll(new LinkedList<>());
        });
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void fetchButtons_withNoLocalButtons_butRemoteButtonsAvailable_shouldYieldTheseRemoteButtons() throws Exception {
        // ### SETUP PHASE ###

        //Setting up mock server to return these buttons below.
        List<MockResponse> expectedResponses = Collections.singletonList
            (new MockResponse().setResponseCode(200).setBody("[\n" +
                "  {\n" +
                "    \"name\": \"Apple\",\n" +
                "    \"type\": \"to_bottom\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"yahoo\",\n" +
                "    \"type\": \"to_left\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"Google\",\n" +
                "    \"type\": \"to_right\"\n" +
                "  }\n" +
                "]"));
        configureMockWebServer(expectedResponses, (server, baseUrl) -> {
            FetchButtonsUseCase fetchButtonsUseCase = setupFetchButtonsUseCase(baseUrl);

            ButtonDao buttonDaoMock = mock(ButtonDao.class);
            //Mocking database to return empty list of buttons.
            when(buttonDaoMock.findAll()).thenReturn(Single.just(new LinkedList<>()));
            //Turning database writes into no-ops.
            doNothing().when(buttonDaoMock).insertAll(ArgumentMatchers.anyList());
            doNothing().when(buttonDaoMock).deleteAll();
            DatabaseResource databaseMock = mock(DatabaseResource.class);
            when(databaseMock.buttonDao()).thenReturn(buttonDaoMock);
            doReturn(databaseMock).when(fetchButtonsUseCase).getDatabase();

            // ### EXECUTION PHASE ###

            final List<ButtonsViewState> states = new LinkedList<>();
            fetchButtonsUseCase.fetchButtons(new FetchButtonsAction())
                    .blockingIterable().forEach(states::add);

            // ### VERIFICATION PHASE ###

            assertThat(states, hasSize(3));
            assertThat(states.get(0), hasProperty("type", equalTo(LOADING)));
            assertThat(states.get(1), hasProperty("type", equalTo(LOADING_SUCCESS))); //local
            assertThat(states.get(2), hasProperty("type", equalTo(LOADING_SUCCESS))); //remote

            assertThat(states.get(1).getButtons(), hasSize(0));

            assertThat(states.get(2).getButtons(), hasSize(3));

            assertThat(states.get(2).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("yahoo")),
                    hasProperty("type", equalTo(TO_LEFT))
                ))
            );

            assertThat(states.get(2).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Apple")),
                    hasProperty("type", equalTo(TO_BOTTOM))
                ))
            );

            assertThat(states.get(2).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Google")),
                    hasProperty("type", equalTo(TO_RIGHT))
                ))
            );

            //Verifying if test made expected API calls.
            assertThat(server.getRequestCount(), is(1));
            String expectedEndpoint = "/" + ApiEndpoints.class
                .getMethod("getButtons").getAnnotation(GET.class).value();
            RecordedRequest request = server.takeRequest();
            assertThat(request.getPath(), is(expectedEndpoint));
            server.shutdown();

            //Verifying if test made expected database operations.
            verify(buttonDaoMock).findAll();
            verify(buttonDaoMock).deleteAll();
            List<Button> buttonsExpectedToHaveBeenStored = new LinkedList<>();
            buttonsExpectedToHaveBeenStored.add(new Button("Apple", TO_BOTTOM));
            buttonsExpectedToHaveBeenStored.add(new Button("yahoo", TO_LEFT));
            buttonsExpectedToHaveBeenStored.add(new Button("Google", TO_RIGHT));
            verify(buttonDaoMock).insertAll(buttonsExpectedToHaveBeenStored);
        });
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void fetchButtons_someLocalButtons_butRemoteButtonsAvailable_shouldOverrideWithRemoteButtons() throws Exception {
        // ### SETUP PHASE ###

        //Setting up mock server to return these buttons below.
        List<MockResponse> expectedResponses = Collections.singletonList
            (new MockResponse().setResponseCode(200).setBody("[\n" +
                "  {\n" +
                "    \"name\": \"Apple\",\n" +
                "    \"type\": \"to_bottom\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"yahoo\",\n" +
                "    \"type\": \"to_left\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"Google\",\n" +
                "    \"type\": \"to_right\"\n" +
                "  }\n" +
                "]"));
        configureMockWebServer(expectedResponses, (server, baseUrl) -> {
            FetchButtonsUseCase fetchButtonsUseCase = setupFetchButtonsUseCase(baseUrl);

            ButtonDao buttonDaoMock = mock(ButtonDao.class);
            //Mocking database to return these buttons below.
            List<Button> buttonsFoundInDatabase = new LinkedList<>();
            buttonsFoundInDatabase.add(new Button("Yahoo!!", TO_LEFT));
            buttonsFoundInDatabase.add(new Button("GitHub", TO_RIGHT));
            buttonsFoundInDatabase.add(new Button("Bitbucket", TO_BOTTOM));
            buttonsFoundInDatabase.add(new Button("GitLab", TO_LEFT));
            buttonsFoundInDatabase.add(new Button("Google", TO_BOTTOM));
            when(buttonDaoMock.findAll()).thenReturn(Single.just(buttonsFoundInDatabase));
            //Turning database writes into no-ops.
            doNothing().when(buttonDaoMock).insertAll(ArgumentMatchers.anyList());
            doNothing().when(buttonDaoMock).deleteAll();
            DatabaseResource databaseMock = mock(DatabaseResource.class);
            when(databaseMock.buttonDao()).thenReturn(buttonDaoMock);
            doReturn(databaseMock).when(fetchButtonsUseCase).getDatabase();

            // ### EXECUTION PHASE ###

            final List<ButtonsViewState> states = new LinkedList<>();
            fetchButtonsUseCase.fetchButtons(new FetchButtonsAction())
                .blockingIterable().forEach(states::add);

            // ### VERIFICATION PHASE ###

            assertThat(states, hasSize(3));
            assertThat(states.get(0), hasProperty("type", equalTo(LOADING)));
            assertThat(states.get(1), hasProperty("type", equalTo(LOADING_SUCCESS))); //local
            assertThat(states.get(2), hasProperty("type", equalTo(LOADING_SUCCESS))); //remote

            assertThat(states.get(1).getButtons(), hasSize(5));

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("GitLab")),
                    hasProperty("type", equalTo(TO_LEFT))
                ))
            );

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Bitbucket")),
                    hasProperty("type", equalTo(TO_BOTTOM))
                ))
            );

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Google")),
                    hasProperty("type", equalTo(TO_BOTTOM))
                ))
            );

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Yahoo!!")),
                    hasProperty("type", equalTo(TO_LEFT))
                ))
            );

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("GitHub")),
                    hasProperty("type", equalTo(TO_RIGHT))
                ))
            );

            assertThat(states.get(2).getButtons(), hasSize(3));

            assertThat(states.get(2).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("yahoo")),
                    hasProperty("type", equalTo(TO_LEFT))
                ))
            );

            assertThat(states.get(2).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Google")),
                    hasProperty("type", equalTo(TO_RIGHT))
                ))
            );

            assertThat(states.get(2).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Apple")),
                    hasProperty("type", equalTo(TO_BOTTOM))
                ))
            );

            //Verifying if test made expected API calls.
            assertThat(server.getRequestCount(), is(1));
            String expectedEndpoint = "/" + ApiEndpoints.class
                .getMethod("getButtons").getAnnotation(GET.class).value();
            RecordedRequest request = server.takeRequest();
            assertThat(request.getPath(), is(expectedEndpoint));
            server.shutdown();

            //Verifying if test made expected database operations.
            verify(buttonDaoMock).findAll();
            verify(buttonDaoMock).deleteAll();
            List<Button> buttonsExpectedToHaveBeenStored = new LinkedList<>();
            buttonsExpectedToHaveBeenStored.add(new Button("Apple", TO_BOTTOM));
            buttonsExpectedToHaveBeenStored.add(new Button("yahoo", TO_LEFT));
            buttonsExpectedToHaveBeenStored.add(new Button("Google", TO_RIGHT));
            verify(buttonDaoMock).insertAll(buttonsExpectedToHaveBeenStored);
        });
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void fetchButtons_someLocalButtons_butFailedToFetchRemoteButtons_shouldPreserveLocalButtons() throws Exception {
        // ### SETUP PHASE ###

        //Setting up mock server to return these buttons below.
        List<MockResponse> expectedResponses = Collections.singletonList
            (new MockResponse().setResponseCode(500));
        configureMockWebServer(expectedResponses, (server, baseUrl) -> {
            FetchButtonsUseCase fetchButtonsUseCase = setupFetchButtonsUseCase(baseUrl);

            ButtonDao buttonDaoMock = mock(ButtonDao.class);
            //Mocking database to return these buttons below.
            List<Button> buttonsFoundInDatabase = new LinkedList<>();
            buttonsFoundInDatabase.add(new Button("Yahoo!!", TO_LEFT));
            buttonsFoundInDatabase.add(new Button("GitHub", TO_RIGHT));
            buttonsFoundInDatabase.add(new Button("Bitbucket", TO_BOTTOM));
            buttonsFoundInDatabase.add(new Button("GitLab", TO_LEFT));
            buttonsFoundInDatabase.add(new Button("Google", TO_BOTTOM));
            when(buttonDaoMock.findAll()).thenReturn(Single.just(buttonsFoundInDatabase));
            //Turning database writes into no-ops.
            doNothing().when(buttonDaoMock).insertAll(ArgumentMatchers.anyList());
            doNothing().when(buttonDaoMock).deleteAll();
            DatabaseResource databaseMock = mock(DatabaseResource.class);
            when(databaseMock.buttonDao()).thenReturn(buttonDaoMock);
            doReturn(databaseMock).when(fetchButtonsUseCase).getDatabase();

            // ### EXECUTION PHASE ###

            final List<ButtonsViewState> states = new LinkedList<>();
            fetchButtonsUseCase.fetchButtons(new FetchButtonsAction())
                    .blockingIterable().forEach(states::add);

            // ### VERIFICATION PHASE ###

            assertThat(states, hasSize(3));
            assertThat(states.get(0), hasProperty("type", equalTo(LOADING)));
            assertThat(states.get(1), hasProperty("type", equalTo(LOADING_SUCCESS))); //local
            assertThat(states.get(2), hasProperty("type", equalTo(LOADING_FAILURE))); //remote

            assertThat(states.get(1).getButtons(), hasSize(5));

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("GitLab")),
                    hasProperty("type", equalTo(TO_LEFT))
                ))
            );

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Bitbucket")),
                    hasProperty("type", equalTo(TO_BOTTOM))
                ))
            );

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Google")),
                    hasProperty("type", equalTo(TO_BOTTOM))
                ))
            );

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("Yahoo!!")),
                    hasProperty("type", equalTo(TO_LEFT))
                ))
            );

            assertThat(states.get(1).getButtons(),
                hasItem(allOf(
                    isA(Button.class),
                    hasProperty("name", equalTo("GitHub")),
                    hasProperty("type", equalTo(TO_RIGHT))
                ))
            );

            //Verifying if test made expected API calls.
            assertThat(server.getRequestCount(), is(1));
            String expectedEndpoint = "/" + ApiEndpoints.class
                .getMethod("getButtons").getAnnotation(GET.class).value();
            RecordedRequest request = server.takeRequest();
            assertThat(request.getPath(), is(expectedEndpoint));
            server.shutdown();

            //Verifying if test made expected database operations.
            verify(buttonDaoMock).findAll();
            verify(buttonDaoMock, times(0)).deleteAll();
            verify(buttonDaoMock, times(0)).insertAll(anyList());
        });
    }

}
