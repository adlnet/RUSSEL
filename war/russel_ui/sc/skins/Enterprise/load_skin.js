/*============================================================
    "Enterprise" theme programmatic settings
    Copyright 2003 and beyond, Isomorphic Software
============================================================*/


isc.loadSkin = function (theWindow) {
if (theWindow == null) theWindow = window;
with (theWindow) {


//----------------------------------------
// Specify skin directory
//----------------------------------------
    // must be relative to your application file or isomorphicDir
    isc.Page.setSkinDir("[ISOMORPHIC]/skins/Enterprise/");


//----------------------------------------
// Load skin style sheet(s)
//----------------------------------------
    isc.Page.loadStyleSheet("[SKIN]/skin_styles.css", theWindow);

    var useCSS3 = isc.Browser.useCSS3;

    if (useCSS3) {

        isc.Canvas.setProperties({
            // this skin uses custom scrollbars
            groupBorderCSS:"1px solid #165fa7",
            showCustomScrollbars:true
        });


        if (isc.Browser.isIE && isc.Browser.version >= 7 && !isc.Browser.isIE9) {
            isc.Canvas.setAllowExternalFilters(false);
            isc.Canvas.setNeverUseFilters(true);

            if (isc.Window) {
                isc.Window.addProperties({
                    modalMaskOpacity:null,
                    modalMaskStyle:"normal"
                });
                isc.Window.changeDefaults("modalMaskDefaults", { src:"[SKIN]opacity.png" });
            }
        }

        if (isc.RPCManager) {
            isc.RPCManager.addClassProperties({
                promptStyle:"cursor"
            });
        }

        //----------------------------------------
        // 1) Scrollbars
        //----------------------------------------
        isc.SimpleScrollThumb.addProperties({
            imageWidth:10, imageHeight:10,
            baseStyle:"scrollThumb",
            hSrc:"[SKIN]hthumb_grip.png",
            vSrc:"[SKIN]vthumb_grip.png"
        });

        isc.Scrollbar.addProperties({
            baseStyle:"scrollbar",
            btnSize:18,
            hSrc:"[SKIN]hscroll.png",
            hThumbClass:isc.HSimpleScrollThumb,
            showRollOver:true,
            thumbInset:0,
            thumbMinSize:20,
            thumbOverlap:2,
            vSrc:"[SKIN]vscroll.png",
            vThumbClass:isc.VSimpleScrollThumb
        });


        //----------------------------------------
        // 2) Buttons
        //----------------------------------------
        isc.Button.addProperties({
            height:22,
            baseStyle:"button"
        });

        // define IButton so examples that support the new SmartClient skin image-based
        // button will fall back on the CSS-based Button with this skin
        isc.ClassFactory.defineClass("IButton", "Button").addProperties({
            baseStyle:"buttonRounded"
        });
        isc.ClassFactory.defineClass("IAutoFitButton", "AutoFitButton").addProperties({
            baseStyle:"buttonRounded"
        });

        if (isc.IButton.markAsFrameworkClass != null) isc.IButton.markAsFrameworkClass();
        if (isc.IAutoFitButton.markAsFrameworkClass != null) isc.IAutoFitButton.markAsFrameworkClass();

        isc.ClassFactory.defineClass("HeaderMenuButton", "IButton").addProperties({
            baseStyle:"headerButton"
        });

        // Have IMenuButton be just a synonym for IMenuButton
        if (isc.MenuButton) {
            isc.ClassFactory.overwriteClass("IMenuButton", "MenuButton");

            if (isc.IMenuButton.markAsFrameworkClass != null) isc.IMenuButton.markAsFrameworkClass();

            isc.MenuButton.addProperties({
                // copy the header (.button) background-color to match when sort arrow is hidden
                baseStyle:"button"
            });
            if (isc.ITreeMenuButton) {
                isc.ClassFactory.overwriteClass("ITreeMenuButton", "TreeMenuButton");
                if (isc.ITreeMenuButton.markAsFrameworkClass != null) {
                    isc.ITreeMenuButton.markAsFrameworkClass();
                }                    
            }
            
        }

        if (isc.MenuButton) {
            isc.MenuButton.addProperties({
                baseStyle:"menuButton",
                iconHeight:4,
                iconWidth:7,
                menuButtonImage:"[SKIN]menu_button.png",
                menuButtonImageUp:"[SKIN]menu_button_up.png",
                showFocusedAsOver:true
            });
        }

        if (isc.IMenuButton) {
            isc.IMenuButton.addProperties({
                capSize:4,
                height:22,
                iconWidth:7,
                iconHeight:4,
                menuButtonImage:"[SKIN]menu_button.png",
                menuButtonImageUp:"[SKIN]menu_button_up.png",
                showFocused:true,
                showFocusedAsOver:true,
                src:"[SKIN]button/button.png",
                titleStyle:"buttonTitle",
                vertical:false,
                width:100
            });
        }

        if (isc.Menu) {
            isc.Menu.addProperties({
                bodyBackgroundColor:null,
                bodyStyleName:"gridBody",
                cellHeight:22,
                checkmarkDisabledImage:{src:"[SKIN]check_disabled.png", width:7, height:6},
                checkmarkImage:{src:"[SKIN]check.png", width:9, height:8},
                fastCellUpdates:false,
                iconBodyStyleName:"menuMain",
                shadowDepth:5,
                showEdges:false,
                showShadow:false,
                submenuDisabledImage:{src:"[SKIN]submenu_disabled.png", height:7, width:4},
                submenuImage:{src:"[SKIN]submenu.png", height:7, width:4}
            });

            isc.addProperties(isc.Menu.ICON_FIELD, {
                baseStyle:"menuIconField",
                width:24
            });

            isc.Menu.TITLE_FIELD.baseStyle = "menuTitleField";
        }

        if (isc.PickTreeItem) {
            isc.PickTreeItem.addProperties({
                buttonDefaults:{ height:21 }
            });
        }

        isc.Label.addProperties({
            showFocused:false
        });

        //----------------------------------------
        // 3) Resizebars
        //----------------------------------------
        // StretchImgSplitbar class renders as resize bar
        isc.StretchImgSplitbar.addProperties({
            capSize:10,
            showGrip:true,
            showOver:false
        });

        isc.Snapbar.addProperties({
            hBaseStyle:"hSplitbar",
            vBaseStyle:"vSplitbar",
            gripBreadth:3,
            gripLength:20,
            hSrc:"[SKIN]hsplit.png",
            items:[
                {name:"blank", width:"*", height:"*"}
            ],
            showClosedGrip:false,
            showDown:false,
            showDownGrip:false,
            showRollOver:false,
            vSrc:"[SKIN]vsplit.png"
        });

        isc.Layout.addProperties({
            resizeBarSize:5,
            // Use the Snapbar as a resizeBar by default - subclass of Splitbar that
            // shows interactive (closed/open) grip images
            // Other options include the Splitbar, StretchImgSplitbar or ImgSplitbar
            resizeBarClass:"Snapbar"
        })

        if (isc.SectionItem) {
            isc.SectionItem.addProperties({
                height:26
            });
        }
        if (isc.SectionStack) {
            isc.SectionStack.addProperties({
                headerHeight:26
            });
        }

        if (isc.ListGrid) {
            isc.ListGrid.addProperties({
                alternateRecordStyles:true,
                alternateBodyStyleName:null,
                backgroundColor:null,
                cellHeight:22,
                checkboxFieldImageHeight:13,
                checkboxFieldImageWidth:13,
                editFailedCSSText:"color:FF6347;",
                errorIconSrc:"[SKINIMG]actions/exclamation.png",
                expansionFieldImageHeight:16,
                expansionFieldImageWidth:16,
                expansionFieldFalseImage:"[SKINIMG]/ListGrid/row_collapsed.png",
                expansionFieldTrueImage:"[SKINIMG]/ListGrid/row_expanded.png",
                expansionFieldImageWidth: 16,
                expansionFieldImageHeight: 16,
                groupIcon:"[SKINIMG]/ListGrid/group.png",
                groupIconPadding:3,
                groupLeadingIndent:1,
                headerBackgroundColor:null,
                headerBaseStyle:"headerButton",
                headerHeight:23,
                headerMenuButtonIcon:"[SKINIMG]ListGrid/sort_descending.png",
                headerMenuButtonConstructor:"HeaderMenuButton",
                headerMenuButtonWidth:17,
                normalCellHeight:22,
                showHeaderMenuButton:true,
                sortAscendingImage:{src:"[SKINIMG]ListGrid/sort_ascending.png", width:9, height:6},
                sortDescendingImage:{src:"[SKINIMG]ListGrid/sort_descending.png", width:9, height:6},
                summaryRowHeight:21,
                tallBaseStyle:"tallCell"
            });
        }

        if (isc.TreeGrid) {
            isc.TreeGrid.addProperties({
                alternateRecordStyles:false,
                folderIcon:"[SKIN]folder.png",
                manyItemsImage:"[SKIN]folder_file.png",
                nodeIcon:"[SKIN]file.png",
                normalBaseStyle:"treeCell",
                openerIconSize:22,
                openerImage:"[SKIN]opener.png",
                sortAscendingImage:{src:"[SKINIMG]ListGrid/sort_ascending.png", width:9, height:6},
                sortDescendingImage:{src:"[SKINIMG]ListGrid/sort_descending.png", width:9, height:6},
                tallBaseStyle:"treeTallCell"
            });
        }
        
        if (isc.MultiSortPanel) {
            isc.MultiSortPanel.changeDefaults("levelUpButtonDefaults", {
                src: "[SKINIMG]TransferIcons/up.png",
                height: 22,
                width: 24
            });
            isc.MultiSortPanel.changeDefaults("levelDownButtonDefaults", {
                src: "[SKINIMG]TransferIcons/down.png",
                height: 22,
                width: 24
            });
        }

        if (isc.TabSet) {
            isc.TabSet.addProperties({
                closeTabIconSize:12,
                paneContainerClassName:"tabSetContainer",
                paneMargin:5,
                pickerButtonSize:20,
                pickerButtonSrc:"[SKIN]picker.png",
                showScrollerRollOver:false,
                scrollerButtonSize:19,
                scrollerSrc:"[SKIN]scroll.png",
                showEdges:false,
                symmetricScroller:false,
                symmetricPickerButton:false,
                tabBarThickness:24,
                useSimpleTabs:true
            });

            // In Netscape Navigator 4.7x, set the backgroundColor directly since the css
            // background colors are not reliable
            if (isc.Browser.isNav) {
                isc.TabSet.addProperties({paneContainerDefaults:{backgroundColor:"#FFFFFF"}});
            }

            isc.TabBar.addProperties({
                baseLineConstructor:"Canvas",
                baseLineProperties:{ backgroundColor:"#C0C3C7", height:1, overflow:"hidden" },
                baseLineThickness:1,
                bottomStyleName:"tabBarBottom",
                layoutEndMargin:5,
                layoutStartMargin:5,
                leadingMargin:5,
                leftStyleName:"tabBarLeft",
                membersMargin:1,
                rightStyleName:"tabBarRight",
                styleName:"tabBar",
                topStyleName:"tabBarTop"
            });
        }

        if (isc.ImgTab) isc.ImgTab.addProperties({capSize:6});

        if (isc.Window) {
            isc.Window.addProperties({
                backgroundColor:null,
                bodyStyle:"windowBody",
                layoutBottomMargin:4,
                layoutLeftMargin:4,
                layoutRightMargin:4,
                layoutTopMargin:1,
                modalMaskOpacity:10,
                membersMargin:0,
                styleName:"windowBackground",
                showHeaderBackground:false,
                showFooter:false
            });

            isc.Window.changeDefaults("headerDefaults", {
                height:20,
                layoutMargin:0
            });

            isc.Window.changeDefaults("resizerDefaults", { src:"[SKIN]/Window/resizer.png" });

            isc.Window.changeDefaults("headerIconDefaults", {
                height:15,
                src:"[SKIN]/Window/headerIcon.png",
                width:15
            });

            isc.Window.changeDefaults("restoreButtonDefaults", {
                height:15,
                showDown:false,
                showRollOver:true,
                src:"[SKIN]/headerIcons/cascade.png",
                width:15
            });

            isc.Window.changeDefaults("closeButtonDefaults", {
                height:15,
                showDown:false,
                showRollOver:true,
                src:"[SKIN]/headerIcons/close.png",
                width:15
            });

            isc.Window.changeDefaults("maximizeButtonDefaults", {
                height:15,
                showRollOver:true,
                src:"[SKIN]/headerIcons/maximize.png",
                width:15
            });

            isc.Window.changeDefaults("minimizeButtonDefaults", {
                height:15,
                showDown:false,
                showRollOver:true,
                src:"[SKIN]/headerIcons/minimize.png",
                width:15
            });

            isc.Window.changeDefaults("toolbarDefaults", { buttonConstructor:"IButton" });

            if (isc.ColorPicker) {
                isc.ColorPicker.addProperties({
                    layoutMargin:2
                });
            }
        }

        if (isc.Dialog) {
            isc.Dialog.addProperties({
                bodyColor:"#FFFFFF",
                bodyStyle:"windowBody",
                layoutBottomMargin:4,
                layoutLeftMargin:4,
                layoutRightMargin:4,
                layoutTopMargin:1,
                modalMaskOpacity:10,
                membersMargin:0,
                styleName:"windowBackground",
                showHeaderBackground:false,
                showFooter:false
            });

            // even though Dialog inherits from Window, we need a separate changeDefaults block
            // because Dialog defines its own toolbarDefaults
            isc.Dialog.changeDefaults("toolbarDefaults", {
                buttonConstructor:"IButton",
                height:42, // 10px margins + 22px button
                membersMargin:10
            });
            
            if (isc.Dialog.Warn && isc.Dialog.Warn.toolbarDefaults) {
                isc.addProperties(isc.Dialog.Warn.toolbarDefaults, {
                    buttonConstructor:"IButton",
                    height:42,
                    membersMargin:10
                });
            }
            
                        
            // Modify the prompt dialog to show a header
            // In the css3-off mode header media is part of the background image, so
            // a header appears to show even though there's no true header widget.
            if (isc.Dialog.Prompt) {
                isc.addProperties(isc.Dialog.Prompt, {
                    showHeader:true,
                    showTitle:false,
                    showCloseButton:false,
                    bodyStyle:"windowBody"
                    
                });
            }
            
        }

        // Dynamic form skinning
        if (isc.SectionHeader) {
            isc.SectionHeader.addProperties({
                icon:"[SKIN]/SectionHeader/opener.png"
            });
        }

        if (isc.FormItem) {
            isc.FormItem.addProperties({
                defaultIconSrc:"[SKIN]/DynamicForm/default_formItem_icon.png",
                errorIconSrc:"[SKINIMG]actions/exclamation.png",
                iconHeight:18,
                iconVAlign:"middle",
                iconWidth:18
            });
        }

        if (isc.CheckboxItem) {
            isc.CheckboxItem.addProperties({
                checkedImage:"[SKINIMG]/DynamicForm/checked.png",
                partialSelectedImage:"[SKINIMG]/DynamicForm/partialcheck.png",
                showValueIconFocused:false,
                showValueIconOver:false,
                uncheckedImage:"[SKINIMG]/DynamicForm/unchecked.png",
                unsetImage:"[SKINIMG]/DynamicForm/unsetcheck.png",
                valueIconWidth:13,
                valueIconHeight:13
            });
        }

        if (isc.TextItem) {
            isc.TextItem.addProperties({
                height:22,
                showFocused:true
            });
        }

        if (isc.TextAreaItem) {
            isc.TextAreaItem.addProperties({
                showFocused:true
            });
        }

        if (isc.SelectItem) {
            isc.SelectItem.addProperties({
                height:22,
                pickerIconSrc:"[SKIN]/pickers/comboBoxPicker.png",
                pickerIconWidth:18,
                showFocusedPickerIcon:false,
                textBoxStyle:"selectItemText"
            });
        }

        if (isc.ComboBoxItem) {
            isc.ComboBoxItem.addProperties({
                height:22,
                pendingTextBoxStyle:"comboBoxItemPendingText",
                pickerIconSrc:"[SKIN]/pickers/comboBoxPicker.png",
                pickerIconWidth:18,
                showFocusedPickerIcon:false,
                textBoxStyle:"selectItemText"
            });
        }

        // used by SelectItem and ComboBoxItem for picklist
        if (isc.ScrollingMenu) {
            isc.ScrollingMenu.addProperties({
            shadowDepth:5,
            showShadow:false
            });
        }

        if (isc.DateItem) {
            isc.DateItem.addProperties({
                height:22,
                pickerIconHeight:14,
                pickerIconSrc:"[SKIN]/DynamicForm/date_control.png",
                pickerIconWidth:16
            });
        }

        if (isc.SpinnerItem) {
            isc.SpinnerItem.addProperties({
                height:22,
                textBoxStyle:"selectItemText"
            });

            isc.SpinnerItem.INCREASE_ICON = isc.addProperties(isc.SpinnerItem.INCREASE_ICON,
            {
                height:11,
                imgOnly:true,
                showDown:false,
                showFocused:false,
                showRollOver:false,
                src:"[SKIN]/DynamicForm/spinner_control_increase.png",
                width:16
            });

            isc.SpinnerItem.DECREASE_ICON = isc.addProperties(isc.SpinnerItem.DECREASE_ICON,
            {
                height:11,
                imgOnly:true,
                showDown:false,
                showFocused:false,
                showRollOver:false,
                src:"[SKIN]/DynamicForm/spinner_control_decrease.png",
                width:16
            });
        }

        if (isc.PopUpTextAreaItem) {
            isc.PopUpTextAreaItem.addProperties({
                popUpIconHeight:16,
                popUpIconSrc:"[SKIN]/DynamicForm/text_control.gif",
                popUpIconWidth:16
            });
        }
        if (isc.ButtonItem && isc.IButton) {isc.ButtonItem.addProperties({
            showFocused:true,
            showFocusAsOver:false,
            buttonConstructor:isc.IButton,
            height:22
        })}

        if (isc.ToolbarItem && isc.IAutoFitButton) {
            isc.ToolbarItem.addProperties({
                buttonConstructor:isc.IAutoFitButton,
                buttonProperties:{ autoFitDirection:isc.Canvas.BOTH }
            });
        }

        if (isc.DateRangeDialog) {
            isc.DateRangeDialog.changeDefaults("headerIconProperties", { src:"[SKIN]/DynamicForm/date_control.png" });
        }

        if (isc.MiniDateRangeItem) {
            isc.MiniDateRangeItem.changeDefaults("pickerIconDefaults", { src:"[SKIN]/DynamicForm/date_control.png" });
        }

        if (isc.RelativeDateItem) {
            isc.RelativeDateItem.changeDefaults("pickerIconDefaults", { src:"[SKIN]/DynamicForm/date_control.png" });
        }

        // Native FILE INPUT items are rendered differently in Safari from other browsers
        // Don't show standard textbox styling around them as it looks odd
        if (isc.UploadItem && isc.Browser.isSafari) {
            isc.UploadItem.addProperties({
                textBoxStyle:"normal"
            });
        }
        if (isc.DateChooser) {
            isc.DateChooser.addProperties({
                alternateWeekStyles:false,
                backgroundColor:null,
                baseNavButtonStyle:"dateChooserNavButton",
                baseWeekdayStyle:"dateChooserWeekday",
                baseWeekendStyle:"dateChooserWeekend",
                baseBottomButtonStyle:"dateChooserBorderedBottomButton",
                edgeCenterBackgroundColor:"#FFFFFF",
                headerStyle:"dateChooserButton",
                nextMonthIcon:"[SKINIMG]/DateChooser/arrow_right.png",
                nextMonthIconHeight:16,
                nextMonthIconWidth:16,
                nextYearIcon:"[SKINIMG]/DateChooser/doubleArrow_right.png",
                nextYearIconHeight:16,
                nextYearIconWidth:16,
                prevMonthIcon:"[SKINIMG]/DateChooser/arrow_left.png",
                prevMonthIconHeight:16,
                prevMonthIconWidth:16,
                prevYearIcon:"[SKINIMG]/DateChooser/doubleArrow_left.png",
                prevYearIconHeight:16,
                prevYearIconWidth:16,
                showDoubleYearIcon:false,
                showEdges:false,
                skinImgDir:"images/DateChooser/",
                todayButtonHeight:20,
                weekendHeaderStyle:"dateChooserWeekendButton",
                styleName:"dateChooserBorder"
            });
        }
        
        if (isc.ToolStrip) {
            isc.ToolStrip.addProperties({
                defaultLayoutAlign:"center",
                height:30
            });

            isc.ToolStrip.changeDefaults("formWrapperDefaults",{cellPadding:3});
        }
        if (isc.ToolStripMenuButton) {
            
            isc.overwriteClass("ToolStripMenuButton", "MenuButton").addProperties({
                autoFit:true,
                baseStyle:"toolStripButton",
                height:22,
                labelVPad:0,
                showDown:true,
                showRollOver:true,
                showTitle:false
            });
        }

        if (isc.ToolStripButton) {
            
            isc.overwriteClass("ToolStripButton", "Button").addProperties({
                autoFit:true,
                baseStyle:"toolStripButton",
                height:22,
                labelVPad:0,
                showTitle:false,
                showRollOver:true,
                showDown:true,
                title:null
            });
        }

        if (isc.RichTextEditor) {
            isc.RichTextEditor.addProperties({
                showEdges:false,
                styleName:"richTextEditorBorder"
            });
        }
        
        if (isc.EdgedCanvas) {
            isc.EdgedCanvas.addProperties({
                edgeSize:6,
                edgeImage: "[SKINIMG]edges/edge.png"
            });
        }

        if (isc.Slider) {
            isc.Slider.addProperties({
                hThumbStyle:"hSliderThumb",
                hTrackStyle:"hSliderTrack",
                thumbConstructor:"StatefulCanvas",
                thumbThickWidth:14,
                thumbThinWidth:14,
                trackConstructor:"StatefulCanvas",
                trackWidth:5,
                vThumbStyle:"vSliderThumb",
                vTrackStyle:"vSliderTrack"
            });
        }

        if (isc.TileGrid) {
            isc.TileGrid.addProperties({
                showEdges:false,
                styleName:null,
                valuesShowRollOver:true
            });
        }

        if (isc.Calendar) {
            isc.Calendar.changeDefaults("datePickerButtonDefaults", {
                showDown:false,
                showOver:false,
                src:"[SKIN]/DynamicForm/date_control.png"
            });

            isc.Calendar.changeDefaults("controlsBarDefaults", {
                height:10,
                layoutBottomMargin:10
            });

            isc.EventWindow.changeDefaults("resizerDefaults", {
                src:"[SKIN]/Window/v_resizer.png"
            });
            isc.TimelineWindow.changeDefaults("resizerDefaults", {
                src:"[SKIN]/Window/h_resizer.png"
            })


        }

        if (isc.Hover) {
            isc.addProperties(isc.Hover.hoverCanvasDefaults, {
                shadowDepth:5,
                showShadow:false
            });
        }

        //indicate type of media used for various icon types
        isc.pickerImgType = "png";
        isc.transferImgType = "png";
        isc.headerImgType = "png";

        isc.Page.checkBrowserAndRedirect("[SKIN]/unsupported_browser.html");

    } else { // useCSS3 is false


//============================================================
//  Component Skinning
//============================================================
//   1) Scrollbars
//   2) Buttons
//   3) Resizebars
//   4) Sections
//   5) Progressbars
//   6) TabSets
//   7) Windows
//   8) Dialogs
//   9) Pickers
//  10) Menus
//  11) Hovers
//  12) ListGrids
//  13) TreeGrids
//  14) Form controls
//  15) Drag & Drop
//  16) Edges
//  17) Sliders
//  18) TileList
//  19) CubeGrid
//  20) FilterBuilder
//  21) Printing
//  22) ToolStrip
//============================================================


	isc.Canvas.addProperties({
		groupBorderCSS: "1px solid #A7ABB4"
	});

    if(isc.Browser.isIE && isc.Browser.version >= 7 && !isc.Browser.isIE9) {
        isc.Canvas.setAllowExternalFilters(false);
        isc.Canvas.setNeverUseFilters(true);
        if(isc.Window) {
          isc.Window.addProperties({
                modalMaskOpacity:null,
                modalMaskStyle:"normal"
            });
            isc.Window.changeDefaults("modalMaskDefaults", { src : "[SKIN]opacity.png" });
        }
    }

    if(isc.RPCManager) {
        isc.RPCManager.addClassProperties({ promptStyle:"cursor" });
    }

//----------------------------------------
// 1) Scrollbars
//----------------------------------------
    isc.Canvas.addProperties({
        showCustomScrollbars:true,
        scrollbarSize:16,
		cornerSize: 16
    });
    isc.ScrollThumb.addProperties({
        capSize:2,
        vSrc:"[SKIN]vthumb.png",
        hSrc:"[SKIN]hthumb.png",
        showGrip:true,
        gripLength:10,
        gripBreadth:10,
		showRollOver: true,
		//showDown: true,
        backgroundColor:"transparent"
    });
    isc.Scrollbar.addProperties({
        btnSize:18,
        showRollOver:true,
        //showDown: true,
        thumbMinSize:20,
        thumbInset:0,
        thumbOverlap:2,
        backgroundColor:"#FFFFFF",
        vSrc:"[SKIN]vscroll.png",
        hSrc:"[SKIN]hscroll.png"
    });


//----------------------------------------
// 2) Buttons
//----------------------------------------

    // "IButton" is the new standard button class for SmartClient applications. Application
    // code should use IButton instead of Button for all standalone buttons. Other skins may
    // map IButton directly to Button, so this single class will work everywhere. Button remains
    // for internal and advanced uses (eg if you need to mix both CSS-based and image-based
    // standalone buttons in the same application).
    isc.defineClass("IButton", "StretchImgButton").addProperties({
        src:"[SKIN]button/button.png",
        height:22,
        width:100,
        capSize:4,
        vertical:false,
        titleStyle:"buttonTitle",
        showFocused:true,
        showFocusedAsOver:true
    });
    if (isc.IButton.markAsFrameworkClass != null) isc.IButton.markAsFrameworkClass();

    isc.defineClass("IAutoFitButton", "IButton").addProperties({
        autoFit: true,
        autoFitDirection: isc.Canvas.HORIZONTAL
    });
    if (isc.IAutoFitButton.markAsFrameworkClass != null) isc.IAutoFitButton.markAsFrameworkClass();


    isc.ImgButton.addProperties({
        showFocused: true,
        showFocusedAsOver:true

    });

    isc.defineClass("HeaderImgButton", "ImgButton").addProperties({
        showFocused: false,
        showRollOver:false,
        showFocusedAsOver: false,
        showDown:false
    });

	isc.Button.addProperties({
		height:22,
		showFocused: true,
		showFocusedAsOver: false
	});

	isc.Label.addProperties({
		showFocused: false
	});



//----------------------------------------
// 3) Resizebars
//----------------------------------------
    // StretchImgSplitbar class renders as resize bar with
    // end caps, body, grip
    isc.StretchImgSplitbar.addProperties({
        // modify vSrc / hSrc for custom appearance
        //vSrc:"[SKIN]vsplit.gif",
        //hSrc:"[SKIN]hsplit.gif",
        capSize:10,
        showGrip:true
    });

    // ImgSplitbar renders as resizebar with resize grip only
    isc.ImgSplitbar.addProperties({
        // modify these properties for custom appearance
        //vSrc:"[SKIN]vgrip.png",
        //hSrc:"[SKIN]hgrip.png",
        //showDown:true,
        //styleName:"splitbar"
    });

    isc.Snapbar.addProperties({
        vSrc:"[SKIN]vsplit.png",
        hSrc:"[SKIN]hsplit.png",
        baseStyle:"splitbar",
	    /*items : [
    	    {name:"blank", width:"capSize", height:"capSize"},
    		{name:"blank", width:"*", height:"*"},
	    	{name:"blank", width:"capSize", height:"capSize"}
        ],*/
        items : [
    	    {name:"bg", width:"*", height:"*"}
        ],
        showDownGrip:false,
        showClosedGrip:false,
        showRollOver:false,
        showDown:false,
        gripBreadth:3,
        gripLength:20
        //capSize:8
    });

    isc.Layout.addProperties({
        resizeBarSize:5,
        // Use the Snapbar as a resizeBar by default - subclass of Splitbar that
        // shows interactive (closed/open) grip images
        // Other options include the Splitbar, StretchImgSplitbar or ImgSplitbar
        resizeBarClass:"Snapbar"
    });


//----------------------------------------
// 4) Sections
//----------------------------------------
    if (isc.SectionItem) {
        isc.SectionItem.addProperties({
            sectionHeaderClass:"ImgSectionHeader",
            height:26
        });
    }
    if (isc.SectionStack) {

        isc.SectionStack.addProperties({
            backgroundColor:null,
            sectionHeaderClass:"ImgSectionHeader",
            headerHeight:26
        });
        isc.ImgSectionHeader.changeDefaults("backgroundDefaults", {
            showRollOver:false,
            showDown:false,
            showDisabledIcon:false,
            showRollOverIcon:false,
            src:"[SKIN]SectionHeader/header.png",
            icon:"[SKIN]SectionHeader/opener.png",
			iconSize: 16,
            capSize:2,
            titleStyle:"imgSectionHeaderTitle",
            baseStyle:"imgSectionHeader",
            backgroundColor:"transparent"
        });
        isc.SectionHeader.addProperties({
            icon:"[SKIN]SectionHeader/opener.png",
			iconSize: 16
        });
    }


//----------------------------------------
// 5) Progressbars
//----------------------------------------
    if (isc.Progressbar) {
        isc.Progressbar.addProperties({
            horizontalItems: [
            {name:"h_start",size:2},
            {name:"h_stretch",size:0},
            {name:"h_end",size:2},
            {name:"h_empty_start",size:2},
            {name:"h_empty_stretch",size:0},
            {name:"h_empty_end",size:2}
            ],
            verticalItems: [
            {name:"v_empty_start",size:2},
            {name:"v_empty_stretch",size:0},
            {name:"v_empty_end",size:0},
            {name:"v_start",size:2},
            {name:"v_stretch",size:0},
            {name:"v_end",size:2}
            ],
            breadth:24,
            length : 300
        });
    }


//----------------------------------------
// 6) TabSets
//----------------------------------------
    if (isc.TabSet) {
        isc.TabSet.addProperties({
            tabBarThickness:24,
            scrollerButtonSize:19,
            pickerButtonSize:20,

            symmetricScroller:false,
            symmetricPickerButton:false,

            scrollerSrc:"[SKIN]scroll.png",
            pickerButtonSrc:"[SKIN]picker.png",

            closeTabIconSize:10,

            showEdges:false,
            paneContainerClassName:"tabSetContainer",

            paneMargin:5,

            showScrollerRollOver: false
        });
        isc.TabSet.changeDefaults("paneContainerDefaults", {
            showEdges:false
        });
        isc.TabBar.addProperties({
            membersMargin:1,

            // keep the tabs from reaching the curved edge of the pane (regardless of align)
            layoutStartMargin:5,
            layoutEndMargin:5,

            styleName:"tabBar",

            // have the baseline overlap the top edge of the TabSet, using rounded media
            baseLineConstructor:"Canvas",
            baseLineProperties : {
                backgroundColor: "#C0C3C7",
                overflow:"hidden",
                height:1
            }

            /*baseLineSrc:"[SKIN]baseline.png",
            baseLineThickness:3,
            baseLineCapSize:4*/

        });
    }
    if (isc.ImgTab) {
        isc.ImgTab.addProperties({
            src:"[SKIN]tab.png",
            capSize:6,
            showRollOver:true,
            showDown:false,
            showDisabled:true,
            showDisabledIcon:false,
            titleStyle:"tabTitle"
        });
    }


//----------------------------------------
// 7) Windows
//----------------------------------------
    if (isc.Window) {
        isc.Window.addProperties({
            // rounded frame edges
            showEdges:true,
            edgeImage: "[SKINIMG]Window/window.png",
            customEdges:null,
            edgeSize:6,
            edgeTop:23,
            edgeBottom:6,
			edgeOffsetTop:2,
			edgeOffsetRight:5,
			edgeOffsetBottom:5,
			minimizeHeight:29,
            showHeaderBackground:false, // part of edges
            showHeaderIcon:true,

            // clear backgroundColor and style since corners are rounded
            backgroundColor:null,
			border: null,
            styleName:"normal",
            edgeCenterBackgroundColor:"#FFFFFF",
            bodyColor:"transparent",
            bodyStyle:"windowBody",

            layoutMargin:0,
            membersMargin:0,

            showFooter:false,

            showShadow:false,
            shadowDepth:5
        });

        isc.Window.changeDefaults("headerDefaults", {
            layoutMargin:0,
            height:20
        });
        isc.Window.changeDefaults("resizerDefaults", {
            src:"[SKIN]/Window/resizer.png"
        });

        isc.Window.changeDefaults("headerIconDefaults", {
            width:15,
            height:15,
            src:"[SKIN]/Window/headerIcon.png"
        });
        isc.Window.changeDefaults("restoreButtonDefaults", {
             src:"[SKIN]/headerIcons/cascade.png",
             showRollOver:true,
             showDown:false,
             width:15,
             height:15
        });
        isc.Window.changeDefaults("closeButtonDefaults", {
             src:"[SKIN]/headerIcons/close.png",
             showRollOver:true,
             showDown:false,
             width:15,
             height:15
        });
        isc.Window.changeDefaults("maximizeButtonDefaults", {
             src:"[SKIN]/headerIcons/maximize.png",
             showRollOver:true,
             width:15,
             height:15
        });
        isc.Window.changeDefaults("minimizeButtonDefaults", {
             src:"[SKIN]/headerIcons/minimize.png",
             showRollOver:true,
             showDown:false,
             width:15,
             height:15
        });
        isc.Window.changeDefaults("toolbarDefaults", {
            buttonConstructor: "IButton"
        });


        if (isc.ColorPicker) {
            isc.ColorPicker.addProperties({
                layoutMargin:0
            });
        }

//----------------------------------------
// 8) Dialogs
//----------------------------------------
        if (isc.Dialog) {
            isc.Dialog.addProperties({
                bodyColor:"transparent",
                hiliteBodyColor:"transparent"
            });
            // even though Dialog inherits from Window, we need a separate changeDefaults block
            // because Dialog defines its own toolbarDefaults
            isc.Dialog.changeDefaults("toolbarDefaults", {
                buttonConstructor: "IButton",
                height:42, // 10px margins + 22px button
                membersMargin:10
            });
            if (isc.Dialog.Warn && isc.Dialog.Warn.toolbarDefaults) {
                isc.addProperties(isc.Dialog.Warn.toolbarDefaults, {
                    buttonConstructor: "IButton",
                    height:42,
                    membersMargin:10
                });
            }
        }

    } // end isc.Window


//----------------------------------------
// 9) Pickers
//----------------------------------------
    // add bevels and shadows to all pickers
    isc.__pickerDefaults = {
        showEdges:true,
        edgeSize:6,
        edgeImage: "[SKINIMG]Window/window.png",
        backgroundColor:"#FFFFFF",
        showShadow:false,
        shadowDepth:6,
        shadowOffset:5
    };
    if (isc.ButtonTable) {
        isc.ButtonTable.addProperties({
            backgroundColor:"#FFFFFF"
        });
    }
    if (isc.FormItem) {
        isc.FormItem.changeDefaults("pickerDefaults", isc.__pickerDefaults);
        isc.FormItem.addProperties({
            defaultIconSrc:"[SKIN]/DynamicForm/default_formItem_icon.png"
        });
    }
    if (isc.CheckboxItem) {
        isc.CheckboxItem.addProperties({
            checkedImage:"[SKINIMG]/DynamicForm/checked.png",
            uncheckedImage:"[SKINIMG]/DynamicForm/unchecked.png",
            unsetImage:"[SKINIMG]/DynamicForm/unsetcheck.png",
            partialSelectedImage:"[SKINIMG]/DynamicForm/partialcheck.png",
            valueIconWidth:13,
            valueIconHeight:13,
            showValueIconOver:false,
            showValueIconFocused:false
        });
    }
    if(isc.RelationItem) {
        isc.RelationItem.changeDefaults("removeButtonDefaults", {
            src: "[SKIN]DynamicForm/Remove_icon.png"
        });
    }

    if (isc.DateChooser) {
        isc.DateChooser.addProperties({
            headerStyle:"dateChooserButton",
            weekendHeaderStyle:"dateChooserWeekendButton",
            baseNavButtonStyle:"dateChooserNavButton",
            navButtonConstructor:"Button",
            baseWeekdayStyle:"dateChooserWeekday",
            baseWeekendStyle:"dateChooserWeekend",
            baseBottomButtonStyle:"dateChooserBorderedBottomButton",
            bottomButtonConstructor:"Button",
            alternateWeekStyles:false,

            showEdges:true,

            edgeImage: "[SKINIMG]Window/window.png",
            edgeSize:6,
            edgeTop:26,
            edgeBottom:5,
			edgeOffsetTop:1,
			edgeOffsetRight:5,
			edgeOffsetLeft:5,
			edgeOffsetBottom:5,            

            todayButtonHeight:20,

            headerHeight:24,

            edgeCenterBackgroundColor:"#E5E5E5",
            backgroundColor:null,

            showShadow:false,
            shadowDepth:6,
            shadowOffset:5,

            showDoubleYearIcon:false,
            skinImgDir:"images/DateChooser/",
            prevYearIcon:"[SKIN]doubleArrow_left.png",
            prevYearIconWidth:16,
            prevYearIconHeight:16,
            nextYearIcon:"[SKIN]doubleArrow_right.png",
            nextYearIconWidth:16,
            nextYearIconHeight:16,
            prevMonthIcon:"[SKIN]arrow_left.png",
            prevMonthIconWidth:16,
            prevMonthIconHeight:16,
            nextMonthIcon:"[SKIN]arrow_right.png",
            nextMonthIconWidth:16,
            nextMonthIconHeight:16
        });
    }
    if (isc.MultiFilePicker) {
        isc.MultiFilePicker.addProperties({
            backgroundColor:"#C7C7C7"
        });
    }
    if (isc.RelationPicker) {
        isc.RelationPicker.addProperties({
            backgroundColor:"#C7C7C7"
        });
    }

    // Native FILE INPUT items are rendered differently in Safari from other browsers
    // Don't show standard textbox styling around them as it looks odd
    if (isc.UploadItem && isc.Browser.isSafari) {
        isc.UploadItem.addProperties({
            textBoxStyle:"normal"
        });
    }
//----------------------------------------
// 10) Menus
//----------------------------------------
    if (isc.Menu) {
        isc.Menu.addProperties({
            cellHeight:22,
            fastCellUpdates:false,
            showShadow:false,
            shadowDepth:5,
            showEdges:false,
            submenuImage:{src:"[SKIN]submenu.png", height:7, width:4},
            submenuDisabledImage:{src:"[SKIN]submenu_disabled.png", height:7, width:4},
	        checkmarkImage:{src:"[SKIN]check.png", width:9, height:8},
	        checkmarkDisabledImage:{src:"[SKIN]check_disabled.png", width:7, height:6},
            bodyStyleName:"gridBody",
			iconBodyStyleName:"menuMain",
			iconBodyStyleName_rtl:"menuMainRTL",
            bodyBackgroundColor:null
        });
		isc.addProperties(isc.Menu.ICON_FIELD, {
			width:24,
			baseStyle:"menuIconField"
		});
		isc.Menu.TITLE_FIELD.baseStyle = "menuTitleField";
    }

    if (isc.MenuButton) {
        isc.MenuButton.addProperties({
			baseStyle: "menuButton",
            menuButtonImage:"[SKIN]menu_button.png",
            menuButtonImageUp:"[SKIN]menu_button_up.png",
            iconWidth:7,
            iconHeight:4,
            showFocusedAsOver:true
        });
    }
    if (isc.IMenuButton) {
        isc.IMenuButton.addProperties({

			menuButtonImage:"[SKIN]menu_button.png",
            menuButtonImageUp:"[SKIN]menu_button_up.png",
            iconWidth:7,
            iconHeight:4,

            // Other properties (match IButton)
            src:"[SKIN]button/button.png",
            height:22,
            capSize:4,
            titleStyle:"buttonTitle",
            showFocused:true,
            showFocusedAsOver:true
        });
    }

	if (isc.SelectionTreeMenu) {
		isc.SelectionTreeMenu.addProperties({
			showIcons:false,
			showKeys:false,
            bodyStyleName:"treeMenuBody",
            bodyBackgroundColor:null
		});
	}

//----------------------------------------
// 11) Hovers
//----------------------------------------
    if (isc.Hover) {
        isc.addProperties(isc.Hover.hoverCanvasDefaults, {
            showShadow:false,
            shadowDepth:5
        });
    }


//----------------------------------------
// 12) ListGrids
//----------------------------------------
    if (isc.ListGrid) {
        isc.ListGrid.addProperties({
            alternateBodyStyleName: null,
            alternateRecordStyles: true,
            backgroundColor: null, 
            bodyBackgroundColor: null,
            bodyStyleName: "gridBody",
            cellHeight: 22,
            checkboxFieldImageHeight: 13,
            checkboxFieldImageWidth: 13,
            editFailedCSSText: "color:FF6347;",
            errorIconSrc: "[SKINIMG]actions/exclamation.png",
            expansionFieldFalseImage: "[SKINIMG]/ListGrid/row_collapsed.png",
            expansionFieldTrueImage: "[SKINIMG]/ListGrid/row_expanded.png",
            groupIcon: "[SKINIMG]/ListGrid/group.png",
            groupIconPadding: 3,
            groupLeadingIndent: 1,
            headerBackgroundColor: null,
            headerBarStyle: "headerBar",
            headerBaseStyle: "imgHeaderButton",
            headerButtonConstructor: "ImgButton",
            headerHeight: 23,
			headerMenuButtonConstructor: "HeaderImgButton",
            headerMenuButtonIcon: "[SKINIMG]/ListGrid/sort_descending.png",
            headerMenuButtonIconHeight: 6,
            headerMenuButtonIconWidth: 9,
            headerMenuButtonSrc: "[SKIN]/ListGrid/header_menu.png",
            headerMenuButtonWidth: 17,
            headerTitleStyle:"headerTitle",
            normalCellHeight: 22,
            showHeaderMenuButton: true,
            sortAscendingImage:{src:"[SKIN]sort_ascending.png", width:9, height:6},
            sorterConstructor: "ImgButton",
            sortDescendingImage: {src:"[SKIN]sort_descending.png", width:9, height:6},
            summaryRowHeight: 21,
            summaryRowStyle: "gridSummaryCell",
			tallBaseStyle: "tallCell"
        });
        
        isc.ListGrid.changeDefaults("sorterDefaults", {
            baseStyle:"sorterButton",
            showFocused:false,
            src:"[SKIN]ListGrid/header.png"
        });

        isc.ListGrid.changeDefaults("headerButtonDefaults", {
            showDown:false,
            showFocused:false,
            showTitle:true,
            src:"[SKIN]ListGrid/header.png"
        });

        isc.ListGrid.changeDefaults("headerMenuButtonDefaults", {
            showDown:false,
            showTitle:true,
            src:"[SKIN]ListGrid/header.png"
        });
        
        isc.ListGrid.changeDefaults("summaryRowDefaults", {
            bodyBackgroundColor:null,
            bodyStyleName:"summaryRowBody"
        });
    }

   if (isc.TreeGrid) {
        isc.TreeGrid.addProperties({
            alternateRecordStyles : false,
			tallBaseStyle: "treeTallCell",
			normalBaseStyle: "treeCell",
            openerImage:"[SKIN]opener.png",
            sortAscendingImage:{src:"[SKINIMG]ListGrid/sort_ascending.png", width:9, height:6},
            sortDescendingImage:{src:"[SKINIMG]ListGrid/sort_descending.png", width:9, height:6}
        });
    }

    if (isc.MultiSortPanel) {
        isc.MultiSortPanel.changeDefaults("levelUpButtonDefaults", {
			src: "[SKINIMG]TransferIcons/up.png",
            height: 22,
            width: 24
		});
        isc.MultiSortPanel.changeDefaults("levelDownButtonDefaults", {
			src: "[SKINIMG]TransferIcons/down.png",
            height: 22,
            width: 24
		});
    }
//----------------------------------------
// 13) TreeGrids
//----------------------------------------
    if (isc.TreeGrid) {
        isc.TreeGrid.addProperties({
			openerIconSize: 22,
            folderIcon:"[SKIN]folder.png",
            nodeIcon:"[SKIN]file.png",
            manyItemsImage:"[SKIN]folder_file.png"
        });
    }
    if (isc.ColumnTree) {
        isc.ColumnTree.addProperties({
            folderIcon:"[SKIN]folder.png",
            nodeIcon:"[SKIN]file.png"
        });
    }


//----------------------------------------
// 14) Form controls
//----------------------------------------
    if (isc.FormItem) {isc.FormItem.addProperties({
        defaultIconSrc:"[SKIN]/DynamicForm/default_formItem_icon.png",
        errorIconSrc : "[SKINIMG]actions/exclamation.png",
        iconHeight:18,
        iconWidth:18,
        iconVAlign:"middle"

    })}

    if (isc.PickTreeItem) {isc.PickTreeItem.addProperties({
        buttonDefaults: {
            height:21
        }
    })}

    if (isc.TextItem) {isc.TextItem.addProperties({
        height:22,
        showFocused: true
    })}

    if (isc.TextAreaItem) {isc.TextAreaItem.addProperties({
        showFocused: true
    })}
    if (isc.SelectItem) {isc.SelectItem.addProperties({
        pickListTallBaseStyle:"tallPickListCell",
        textBoxStyle:"selectItemText",
        showFocusedPickerIcon:false,
        pickerIconSrc:"[SKIN]/pickers/comboBoxPicker.png",
        height:22,
        pickerIconWidth:18
    })}

    if (isc.ComboBoxItem) {isc.ComboBoxItem.addProperties({
        pickListTallBaseStyle:"tallPickListCell",
        textBoxStyle:"selectItemText",
        pendingTextBoxStyle:"comboBoxItemPendingText",
        showFocusedPickerIcon:false,
        pickerIconSrc:"[SKIN]/pickers/comboBoxPicker.png",
        height:22,
        pickerIconWidth:18
    })}
    // used by SelectItem and ComboBoxItem for picklist
    if (isc.ScrollingMenu) {isc.ScrollingMenu.addProperties({
        showShadow:false,
        shadowDepth:5
    })}
    if (isc.DateItem) {
        isc.DateItem.addProperties({
            height:22,
            pickerIconWidth:16,
            pickerIconHeight:14,
            pickerIconSrc:"[SKIN]/DynamicForm/date_control.png"
        })
    }
    if (isc.SpinnerItem) {
        isc.SpinnerItem.addProperties({
            textBoxStyle:"selectItemText",
            height:22
        });
        isc.SpinnerItem.INCREASE_ICON = isc.addProperties(isc.SpinnerItem.INCREASE_ICON, {
            width:16,
            height:11,
            showRollOver:true,
            showFocused:true,
            showDown:true,
            imgOnly:true,
            src:"[SKIN]/DynamicForm/spinner_control_increase.png"
        });
        isc.SpinnerItem.DECREASE_ICON = isc.addProperties(isc.SpinnerItem.DECREASE_ICON, {
            width:16,
            height:11,
            showRollOver:true,
            showFocused:true,
            showDown:true,
            imgOnly:true,
            src:"[SKIN]/DynamicForm/spinner_control_decrease.png"
        });
    }



    if (isc.PopUpTextAreaItem) {isc.PopUpTextAreaItem.addProperties({
        popUpIconSrc: "[SKIN]/DynamicForm/text_control.gif",
        popUpIconWidth:16,
        popUpIconHeight:16
    })}
    if (isc.ButtonItem && isc.IButton) {isc.ButtonItem.addProperties({
        showFocused:true,
        showFocusAsOver:false,
        buttonConstructor:isc.IButton,
        height:22
    })}

    if (isc.ToolbarItem && isc.IAutoFitButton) {isc.ToolbarItem.addProperties({
        buttonConstructor:isc.IAutoFitButton,
        buttonProperties: {
            autoFitDirection: isc.Canvas.BOTH
        }
    })}

    if(isc.DateRangeDialog) {
        isc.DateRangeDialog.changeDefaults("headerIconProperties", {
            src: "[SKIN]/DynamicForm/date_control.png"
        });
    }
    if(isc.MiniDateRangeItem) {
        isc.MiniDateRangeItem.changeDefaults("pickerIconDefaults", {
            src: "[SKIN]/DynamicForm/date_control.png"
        });
    }
    if(isc.RelativeDateItem) {
        isc.RelativeDateItem.changeDefaults("pickerIconDefaults", {
            src: "[SKIN]/DynamicForm/date_control.png"
        });
    }
//----------------------------------------
// 15) Drag & Drop
//----------------------------------------
    // drag tracker drop shadow (disabled by default because many trackers are irregular shape)
    //isc.addProperties(isc.EH.dragTrackerDefaults, {
    //    showShadow:false,
    //    shadowDepth:4
    //});
    // drag target shadow and opacity
    isc.EH.showTargetDragShadow = true;
    isc.EH.targetDragOpacity = 50;



//----------------------------------------
// 16) Edges
//----------------------------------------
    // default edge style serves as a pretty component frame/border - just set showEdges:true
    if (isc.EdgedCanvas) {
        isc.EdgedCanvas.addProperties({
            edgeSize:6,
            edgeImage: "[SKINIMG]edges/edge.png"
        });
    }


//----------------------------------------
// 17) Sliders
//----------------------------------------
    if (isc.Slider) {
        isc.Slider.addProperties({
            thumbThickWidth:14,
            thumbThinWidth:14,
            trackWidth:5,
            trackCapSize:2,
            thumbSrc:"thumb.png",
            trackSrc:"track.png"
        });
    }

//----------------------------------------
// 18) TileList
//----------------------------------------
    if (isc.TileGrid) {
        isc.TileGrid.addProperties({
            valuesShowRollOver: true,
            styleName:null,
            showEdges:true
        });
    }

// ----------------------------------------
// 19)  CubeGrid
//----------------------------------------
    if (isc.CubeGrid) {
        isc.CubeGrid.addProperties({
            bodyStyleName:"cubeGridBody",
            alternateBodyStyleName:"alternateCubeGridBody"
        });
    }

// ----------------------------------------
// 20) FilterBuilder
//----------------------------------------
	if (isc.FilterBuilder) {
		isc.FilterBuilder.changeDefaults("addButtonDefaults", {
			showFocused: false
		});
		isc.FilterBuilder.changeDefaults("removeButtonDefaults", {
			showFocused: false
		});
	}

// -------------------------------------------
// 21) Printing
// -------------------------------------------
    if (isc.PrintWindow) {
        isc.PrintWindow.changeDefaults("printButtonDefaults", {
            height: 18
        });
    }

// -------------------------------------------
// 21) Printing
// -------------------------------------------
    if (isc.Calendar) {
        isc.Calendar.changeDefaults("datePickerButtonDefaults", {
            showDown:false,
            showOver : false,
            src:"[SKIN]/DynamicForm/date_control.png"
        });

        isc.Calendar.changeDefaults("controlsBarDefaults", {
            height:10,
            layoutBottomMargin :10
        });
        isc.Calendar.changeDefaults("addEventButtonDefaults", {
            src:"[SKINIMG]actions/plus.png"
        });

        isc.EventWindow.changeDefaults("resizerDefaults", {
            src:"[SKIN]/Window/v_resizer.png"
        });

        isc.TimelineWindow.changeDefaults("resizerDefaults", {
            src:"[SKIN]/Window/h_resizer.png"
        })
    }

// -------------------------------------------
// 22) ToolStrip
// -------------------------------------------
    if(isc.ToolStrip) {
        isc.ToolStrip.addProperties({
            height:30,
            defaultLayoutAlign:"center",
			verticalStyleName:"toolStripVertical"
        });

        isc.ToolStrip.changeDefaults("formWrapperDefaults",
            {cellPadding:3}
        );
    }

// -------------------------------------------
// ExampleViewPane - used in the feature explorer
// -------------------------------------------
    if (isc.ExampleViewPane) {
        isc.ExampleViewPane.addProperties({
            styleName:"normal"
        });
    }

    // specify where the browser should redirect if not supported
    isc.Page.checkBrowserAndRedirect("[SKIN]/unsupported_browser.html");

    } // end useCSS3 else block



    // Skinning not dependent on whether CSS3 is being used.

//----------------------------------------
// 14) Form controls
//----------------------------------------
    if (isc.MultiComboBoxItem) {
        isc.MultiComboBoxItem.changeDefaults("buttonDefaults", {
            icon: "[SKIN]DynamicForm/drop.png",
            iconWidth: 12,
            iconHeight: 12,
            iconSize: 12
        });
    }

    // remember the current skin so we can detect multiple skins being loaded
    if (isc.setCurrentSkin) isc.setCurrentSkin("Enterprise");
}   // end with()
}   // end loadSkin()

isc.loadSkin();