/**
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.sdk.component.studio.view;

import static org.talend.sdk.component.studio.metadata.ITaCoKitElementParameterEventProperties.EVENT_PROPERTY_VALUE_CHANGED;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.talend.commons.ui.gmf.util.DisplayUtils;
import org.talend.core.model.process.EComponentCategory;
import org.talend.core.model.process.Element;
import org.talend.core.model.process.IElementParameter;
import org.talend.designer.core.model.FakeElement;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.views.properties.composites.MissingSettingsMultiThreadDynamicComposite;
import org.talend.sdk.component.studio.metadata.TaCoKitElementParameter;

/**
 * Registers PropertyChangeListener for each IElementParameter during instantiation
 * PropertyChangeListener refreshes layout after each IElementParameter value update
 */
// FIXME probably wrong parent class
public class TaCoKitComposite extends MissingSettingsMultiThreadDynamicComposite {

    /**
     * Reference to refresher listener. It is stored to be removed from parameters, when this composite is disposed
     */
    private PropertyChangeListener refresher;

    /**
     * Also stored for disposal only
     */
    private List<? extends IElementParameter> parameters;

    public TaCoKitComposite(final Composite parentComposite, final int styles, final EComponentCategory section,
            final Element element, final boolean isCompactView) {
        super(parentComposite, styles, section, element, isCompactView);
        init();
    }

    public TaCoKitComposite(final Composite parentComposite, final int styles, final EComponentCategory section,
            final Element element, final boolean isCompactView, final Color backgroundColor) {
        super(parentComposite, styles, section, element, isCompactView, backgroundColor);
        init();
    }

    /**
     * For each {@link TaCoKitElementParameter} registers PropertyChangeListener, which calls
     * {@link this#refresh()} on each {@link TaCoKitElementParameter} value change event
     * 
     * Note, component has special parameter UPDATE_COMPONENTS, which is checked to know whether it is required to
     * refresh layout.
     * So, it should be true to force refresh
     */
    private void init() {
        parameters = elem.getElementParameters();
        refresher = event -> {
            Node node = (Node) elem;
            node.getElementParameter(EParameterName.UPDATE_COMPONENTS.getName()).setValue(Boolean.TRUE);
            refresh();
        };

        elem
                .getElementParameters()
                .stream()
                .filter(p -> p instanceof TaCoKitElementParameter)
                .map(p -> (TaCoKitElementParameter) p)
                .filter(TaCoKitElementParameter::isForceRefresh)
                .forEach(p -> p.registerListener(EVENT_PROPERTY_VALUE_CHANGED, refresher));
    }

    // TODO is it required?
    @Override
    public void refresh() {
        if (elem instanceof FakeElement) { // sync exec
            DisplayUtils.getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    operationInThread();
                }
            });
        } else { // async exec
            super.refresh();
        }
    }

    /**
     * Specifies minimal height of current UI element
     * 
     * @return minimal height
     */
    @Override
    public int getMinHeight() {
        if (minHeight < 200) {
            return 200;
        } else if (minHeight > 700) {
            return 700;
        }
        return minHeight;
    }

    @Override
    public synchronized void dispose() {
        super.dispose();
        parameters
                .stream()
                .filter(p -> p instanceof TaCoKitElementParameter)
                .map(p -> (TaCoKitElementParameter) p)
                .filter(TaCoKitElementParameter::isForceRefresh)
                .forEach(p -> p.unregisterListener(EVENT_PROPERTY_VALUE_CHANGED, refresher));
    }

}
