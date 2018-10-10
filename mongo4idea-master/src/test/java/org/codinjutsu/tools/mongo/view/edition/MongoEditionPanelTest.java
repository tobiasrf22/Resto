/*
 * Copyright (c) 2018 David Boissier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codinjutsu.tools.mongo.view.edition;

import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.fixture.Containers;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codinjutsu.tools.mongo.view.BsonTest;
import org.codinjutsu.tools.mongo.view.JsonTableCellReader;
import org.codinjutsu.tools.mongo.view.MongoPanel;
import org.codinjutsu.tools.mongo.view.MongoResultPanel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MongoEditionPanelTest implements BsonTest {

    private MongoEditionPanel mongoEditionPanel;

    private FrameFixture frameFixture;

    private final MongoPanel.MongoDocumentOperations mockMongoOperations = mock(MongoPanel.MongoDocumentOperations.class);
    private final MongoResultPanel.ActionCallback mockActionCallback = mock(MongoResultPanel.ActionCallback.class);

    @After
    public void tearDown() {
        frameFixture.cleanUp();
    }

    @Before
    public void setUp() {

        mongoEditionPanel = GuiActionRunner.execute(new GuiQuery<MongoEditionPanel>() {
            protected MongoEditionPanel executeInEDT() {
                return new MongoEditionPanel(mockMongoOperations, mockActionCallback) {
                    @Override
                    void buildPopupMenu() {
                    }
                };
            }
        });


    }

    @Test
    public void displayMongoDocumentInTheTreeTable() {
        mongoEditionPanel.updateEditionTree(
                new Document("_id", new ObjectId("50b8d63414f85401b9268b99"))
                        .append("label", "toto")
                        .append("visible", false)
                        .append("image", null));
        frameFixture = Containers.showInFrame(mongoEditionPanel);

        JTableFixture tableFixture = frameFixture.table("editionTreeTable");
        tableFixture.replaceCellReader(new JsonTableCellReader());

        tableFixture.requireColumnCount(2)
                .requireContents(new String[][]{
                        {"_id", "50b8d63414f85401b9268b99"},
                        {"label", "toto"},
                        {"visible", "false"},
                        {"image", "null"}
                });
    }

    @Test
    public void editKeyWithStringValue() {
        mongoEditionPanel.updateEditionTree(
                new Document("_id", new ObjectId("50b8d63414f85401b9268b99"))
                        .append("label", "toto")
                        .append("visible", false)
                        .append("image", null));
        frameFixture = Containers.showInFrame(mongoEditionPanel);

        JTableFixture editionTreeTable = frameFixture.table("editionTreeTable");
        editionTreeTable.replaceCellReader(new JsonTableCellReader());

//        edit 'label' key
        editionTreeTable.cell(TableCell.row(1).column(1))
                .doubleClick()
                .enterValue("Hello");

        mongoEditionPanel.save();

        ArgumentCaptor<Document> argument = ArgumentCaptor.forClass(Document.class);
        verify(mockMongoOperations).updateMongoDocument(argument.capture());

        assertThat(argument.getValue())
                .isEqualTo(new Document("_id", new ObjectId("50b8d63414f85401b9268b99"))
                        .append("label", "Hello")
                        .append("visible", false)
                        .append("image", null));

        verify(mockActionCallback, times(1)).onOperationSuccess(any(String.class), any(String.class));
    }

    @Test
    public void addKeyWithSomeValue() {
        mongoEditionPanel.updateEditionTree(
                new Document("_id", new ObjectId("50b8d63414f85401b9268b99"))
                        .append("label", "toto")
                        .append("visible", false)
                        .append("image", null));
        frameFixture = Containers.showInFrame(mongoEditionPanel);

        JTableFixture editionTreeTable = frameFixture.table("editionTreeTable");

        editionTreeTable.replaceCellReader(new JsonTableCellReader());


        editionTreeTable.selectCell(TableCell.row(1).column(1));
        mongoEditionPanel.addKey("stringKey", "pouet");

        editionTreeTable.selectCell(TableCell.row(1).column(1));
        mongoEditionPanel.addKey("numberKey", "1.1");

        editionTreeTable.requireContents(new String[][]{
                {"_id", "50b8d63414f85401b9268b99"},
                {"label", "toto"},
                {"visible", "false"},
                {"image", "null"},
                {"stringKey", "pouet"},
                {"numberKey", "1.1"},
        });
    }

    @Test
    public void addValueInAList() {
        mongoEditionPanel.updateEditionTree(
                new Document("_id", new ObjectId("50b8d63414f85401b9268b99"))
                        .append("title", "XP by example")
                        .append("tags", Arrays.asList("pair programming", "tdd", "agile"))
                        .append("innerList", Arrays.asList(
                                Arrays.asList(1, 2, 3, 4),
                                Arrays.asList(false , true),
                                Arrays.asList(
                                        new Document("tagName", "pouet"),
                                        new Document("tagName", "paf")
                                )
                        ))
        );
        frameFixture = Containers.showInFrame(mongoEditionPanel);

        JTableFixture editionTreeTable = frameFixture.table("editionTreeTable");

        editionTreeTable.replaceCellReader(new JsonTableCellReader());

        editionTreeTable.requireContents(new String[][]{
                {"_id", "50b8d63414f85401b9268b99"},
                {"title", "XP by example"},
                {"tags", "[\"pair programming\", \"tdd\", \"agile\"]"},
                {"[0]", "pair programming"},
                {"[1]", "tdd"},
                {"[2]", "agile"},
                {"innerList", "[[1, 2, 3, 4], [false, true], [{ \"tagName\" : \"pouet\" }, { \"tagName\" : \"paf\" }]]"},
                {"[0]", "[1, 2, 3, 4]"},
                {"[1]", "[false, true]"},
                {"[2]", "[{ \"tagName\" : \"pouet\" }, { \"tagName\" : \"paf\" }]"}});

        editionTreeTable.selectCell(TableCell.row(3).column(1));
        mongoEditionPanel.addValue("refactor");
        editionTreeTable.requireContents(new String[][]{
                {"_id", "50b8d63414f85401b9268b99"},
                {"title", "XP by example"},
                {"tags", "[\"pair programming\", \"tdd\", \"agile\"]"},
                {"[0]", "pair programming"},
                {"[1]", "tdd"},
                {"[2]", "agile"},
                {"[3]", "refactor"},
                {"innerList", "[[1, 2, 3, 4], [false, true], [{ \"tagName\" : \"pouet\" }, { \"tagName\" : \"paf\" }]]"},
                {"[0]", "[1, 2, 3, 4]"},
                {"[1]", "[false, true]"},
                {"[2]", "[{ \"tagName\" : \"pouet\" }, { \"tagName\" : \"paf\" }]"}});

    }
}
