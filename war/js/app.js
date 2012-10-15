/* Foundation v2.2.1 http://foundation.zurb.com */

/*
Copyright (c) 2012 Eduworks Corporation
All rights reserved.
 
This Software (including source code, binary code and documentation) is provided by Eduworks Corporation to
the Government pursuant to contract number W31P4Q-12 -C- 0119 dated 21 March, 2012 issued by the U.S. Army 
Contracting Command Redstone. This Software is a preliminary version in development. It does not fully operate
as intended and has not been fully tested. This Software is provided to the U.S. Government for testing and
evaluation under the following terms and conditions:

	--Any redistribution of source code, binary code, or documentation must include this notice in its entirety, 
	 starting with the above copyright notice and ending with the disclaimer below.
	 
	--Eduworks Corporation grants the U.S. Government the right to use, modify, reproduce, release, perform,
	 display, and disclose the source code, binary code, and documentation within the Government for the purpose
	 of evaluating and testing this Software.
	 
	--No other rights are granted and no other distribution or use is permitted, including without limitation 
	 any use undertaken for profit, without the express written permission of Eduworks Corporation.
	 
	--All modifications to source code must be reported to Eduworks Corporation. Evaluators and testers shall
	 additionally make best efforts to report test results, evaluation results and bugs to Eduworks Corporation
	 using in-system feedback mechanism or email to russel@eduworks.com.
	 
THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
THE COPYRIGHT HOLDER BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN 
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
*/

/* Return security level color for icon */
function getSecurityColor(s) {
	var $color;
	switch(s) {
		case "Unclassified": 
		case "None":
		case "Statement A (Public)":
			$color = "green"; break;
		
		case "Confidential":
		case "Statement B":
		case "Statement C":
		case "Statement D":
			$color = "yellow"; break;
		
		case "Secret": 
		case "Top Secret": 
		case "For Official Use Only (FOUO)":
		case "Statement E":
		case "Statement F":
		case "Statement X":
			$color = "red"; break;
		
		default:
			return s; break;
	}
	return "<span class='icon "+$color+"'></span>"+s;
}

/* Object Editor / Upload modal - reset the form to 1 input */
function resetAddFileModal() {
	initAddFileModal();
	$("#addFileModal .filesToUpload").empty();
	$inputObj = $resetAddFileModal.clone(true);
	$("#addFileModal .filesToUpload").append($inputObj);
	$inputObj.slideDown('fast');
}

/* Object Editor / Upload modal - clone file browser container */
var $resetAddFileModal;
function initAddFileModal() {
	if (! $resetAddFileModal) {
		$resetAddFileModal = $(".classicFileBrowser").clone(true);
		$resetAddFileModal.css("display", "none");
	}
}

/* Object Details Screen (Empty): show object, hide help */
$("a.addFile").click(function() {
	$('#addFileModal').trigger('reveal:close');
	$('p.help').fadeOut('fast', function() {
		$('.r-obj-small').fadeIn('fast');
		$('#r-metadataToolbar').slideDown('fast');
	});
});

/* Object Editor / Upload modal - reset the form to 1 input */
function resetAddLinkModal() {
	initAddLinkModal();
	$("#addLinkModal .linksToUpload").empty();
	$inputObj = $resetAddLinkModal.clone(true);
	$("#addLinkModal .linksToUpload").append($inputObj);
	$inputObj.slideDown('fast');
}

/* Object Editor / Upload modal - clone link container */
var $resetAddLinkModal;
function initAddLinkModal() {
	if (! $resetAddLinkModal) {
		$resetAddLinkModal = $(".classicURLField").clone(true);
		$resetAddLinkModal.css("display", "none");
	}
}

/* Object Details Screen (Empty): show object, hide help */
$("a.addLink").click(function() {
	$('#addLinkModal').trigger('reveal:close');
	$('p.help').fadeOut('fast', function() {
		$('.r-obj-small').fadeIn('fast');
		$('#r-metadataToolbar').slideDown('fast');
	});
});


function boxedCustomAppJavascript() {
	jQuery(document).ready(function ($) {
	
		/* Use this js doc for all application specific JS */
	
		/* TABS --------------------------------- */
		/* Remove if you don't need :) */
	
		function activateTab($tab) {
			var $activeTab = $tab.closest('dl').find('a.active'),
					contentLocation = $tab.attr("href") + 'Tab';
					
			// Strip off the current url that IE adds
			contentLocation = contentLocation.replace(/^.+#/, '#');
	
			//Make Tab Active
			$activeTab.removeClass('active');
			$tab.addClass('active');
	
	    //Show Tab Content
			$(contentLocation).closest('.tabs-content').children('li').hide();
			$(contentLocation).css('display', 'block');
		}
	
		$('dl.tabs').each(function () {
			//Get all tabs
			var tabs = $(this).children('dd').children('a');
			tabs.click(function (e) {
				activateTab($(this));
			});
		});
	
		if (window.location.hash) {
			activateTab($('a[href="' + window.location.hash + '"]'));
			$.foundation.customForms.appendCustomMarkup();
		}
	
		/* ALERT BOXES ------------ */
		$(".alert-box").delegate("a.close", "click", function(event) {
	    event.preventDefault();
		  $(this).closest(".alert-box").fadeOut(function(event){
		    $(this).remove();
		  });
		});
	
	
		/* PLACEHOLDER FOR FORMS ------------- */
		/* Remove this and jquery.placeholder.min.js if you don't need :) */
	
		$('input, textarea').placeholder();
	
		/* TOOLTIPS ------------ */
		$(this).tooltips();
	
	
	
		/* UNCOMMENT THE LINE YOU WANT BELOW IF YOU WANT IE6/7/8 SUPPORT AND ARE USING .block-grids */
	//	$('.block-grid.two-up>li:nth-child(2n+1)').css({clear: 'left'});
	//	$('.block-grid.three-up>li:nth-child(3n+1)').css({clear: 'left'});
	//	$('.block-grid.four-up>li:nth-child(4n+1)').css({clear: 'left'});
	//	$('.block-grid.five-up>li:nth-child(5n+1)').css({clear: 'left'});
	
	
	
		/* DROPDOWN NAV ------------- */
	
		var lockNavBar = false;
		$('.nav-bar a.flyout-toggle').live('click', function(e) {
			e.preventDefault();
			var flyout = $(this).siblings('.flyout');
			if (lockNavBar === false) {
				$('.nav-bar .flyout').not(flyout).slideUp(500);
				flyout.slideToggle(500, function(){
					lockNavBar = false;
				});
			}
			lockNavBar = true;
		});
	  if (Modernizr.touch) {
	    $('.nav-bar>li.has-flyout>a.main').css({
	      'padding-right' : '75px'
	    });
	    $('.nav-bar>li.has-flyout>a.flyout-toggle').css({
	      'border-left' : '1px dashed #eee'
	    });
	  } else {
	    $('.nav-bar>li.has-flyout').hover(function() {
	      $(this).children('.flyout').show();
	    }, function() {
	      $(this).children('.flyout').hide();
	    })
	  }
	
	
		/* DISABLED BUTTONS ------------- */
		/* Gives elements with a class of 'disabled' a return: false; */
	});
	
	/* -------------------------------------------------------- */
	/* ----- ANDY's PLACEHOLDER SCRIPTS for reference only ---- */
	/* -------------------------------------------------------- */
	
	var selectedTemplateSection;
	var selectedAsset;
	
	/* Delete 3D Tile */
	$('.tile .delete').click(function() {
		tileObj = $(this).closest('.tile');
		$tileCel = $(this).closest('td');
	    tileObj.fadeOut(250, function() {
			tileObj.remove();
			if($tileCel.text().trim()=="") {
				$tileCel.remove(); /* Delete empty cell */
				/* TO DO: shuffle down tiles in columns after this one to fill in any empty cell. */
			}
		});
		return false;
	});
	
	/* ---- RUSSELS screens ---- */
	
	/* Search results: Show more options */
	$('#showSearchOptions').click(function() {
		$(this).remove();
		$('#searchOptions').fadeIn('fast');
		return false;
	});
	
	
	
	
	/* Object Details Screen: Show more details */
	$("#r-metadata-hide").click(function() {
		$(this).slideUp('fast');
		$('#r-metadata-show').slideDown('fast');
		return false;
	});
	
	/* Object Details Screen: Show more details */
	$("#r-metadata .section a.header").click(function() {
		$section = $(this).parent();
		$content = $(this).next();
		
		if ($section.hasClass('collapsed')) {
			$section.removeClass('collapsed');
			$content.slideDown('fast');
		} else {
			$section.addClass('collapsed');
			$content.slideUp('fast');
		}
		return false;
	});
	
	/* $("#r-metadataToolbar .section .value, #r-metadata .meta-value.editable, #r-objectives .meta-value.editable").unbind();  */
	/* Object Details and upload screens - Edit metadata: Switch text to editable input form */
	$("#r-metadataToolbar .section .value, #r-metadata .meta-value.editable, #r-metaDescription .meta-value.editable").click(function() {
		$textObj = $(this);
		$inputObj = $(this).next();
		$objValue = $(this).text();
		if ($.trim($objValue)=="Click to edit") {
			$objValue=null;
		}
		$inputObj.val($objValue);
		$(this).fadeOut(100, function() {
			$(this).next().fadeIn(100);
			$inputObj.focus();
			$inputObj.select();
		});
		return false;
	});
	
	/* Object Details: delete comment */
	$('#r-comments .delete').click(function() {
	    $(this).closest('.user-comment').fadeOut(250, function() {
			$(this).closest('td').remove();
		});
		return false;
	});
	
	
	/* Deselect drop-down menu when value changes */
	$('select').change(function() {
	  $(this).blur();
	});
	
	/*  $("#r-metadataToolbar .section .value-input, #r-metadata .section .value-input, #r-objectives .value-input").unbind(); */
	/* Edit Metadata Screen: Switch input form back to text */
	$("#r-metadataToolbar .section .value-input, #r-metadata .section .value-input, #r-metaDescription .value-input").live('blur', function() {
		$textObj = $(this).prev();
		$inputObj = $(this);
		$objValue = $(this).val();
		if ($.trim($objValue)=="") {
			$objValue = "Click to edit";
		}
		
		checkFouo($objValue);
		
		$objValue = getSecurityColor($objValue);
		
		if ($objValue!="Click to edit") {
			if ($('#r-detailEditUpdate').size()!=0) {
				$('#r-detailEditUpdate').removeClass('white');
				$('#r-detailEditUpdate').addClass('blue');
				$('#r-detailSaveAlert').removeClass('hide');
			}
			
			if ($('#r-editSave').size()!=0) {
				$('#r-editSave').removeClass('white');
				$('#r-editSave').addClass('blue');
				$('#r-save-alert').removeClass('hide');
			}
		}
		
		checkFouo();
		$textObj.html($objValue);
		$inputObj.fadeOut(100, function() {
			$textObj.fadeIn(100);
		});
	});
	
	/* Set and disable Security 'Level' value (FOUO) for any non-public classifications */
	function checkFouo(s) {
		
		/* Desired functionality:
		If user selects "Unclassified" then "Level" (FOUO) is editable.
		For all other Classification the Level is automatically set to "FOUO" and cannot be changed.
		
		If FOUO or any classification except "Unclassified" is selected then 
		   user cannot choose Distribution option "Statement A (Public)", it should be disabled.
		   If it was already selected then switch the value to "Statement B"
		*/
		var classificationElement = document.getElementById("detailMetaClassification");
	    var levelElement = document.getElementById("detailMetaLevel");
	    var distributionElement = document.getElementById("detailMetaDistribution");
		var dist1 = document.getElementById('detailDistribution1');
		var level1 = document.getElementById('detailLevel1');
	    
		var classificationElement2 = document.getElementById("metaClassification");
	    var levelElement2 = document.getElementById("metaLevel");
	    var distributionElement2 = document.getElementById("metaDistribution");
		var dist12 = document.getElementById('distribution1');
		var level12 = document.getElementById('level1');
	    
	    if (classificationElement!=null&&levelElement!=null&&distributionElement!=null) {
			var classificationText = $(classificationElement).text();
			var distributionText = $(distributionElement).text();
			if ((classificationText.toLowerCase()=="unclassified") || (classificationText=="") ||
	                (classificationText.toLowerCase()=="click to edit")) {
	            dist1.disabled = '';
	            level1.disabled = '';
	        } else {
	            $(levelElement).html(getSecurityColor("For Official Use Only (FOUO)"));
	            level1.disabled = 'disabled';
	            if (distributionText.toLowerCase()=="statement a (public)") {
	                $(distributionElement).html(getSecurityColor("Statement B"));
	                dist1.disabled = 'disabled';
	            }
	        }
		}
		
		if (classificationElement2!=null&&levelElement2!=null&&distributionElement2!=null) {
			var classificationText = $(classificationElement2).text();
			var distributionText = $(distributionElement2).text();
			if ((classificationText.toLowerCase()=="unclassified") || (classificationText=="") ||
	                (classificationText.toLowerCase()=="click to edit")) {
	            dist12.disabled = '';
	            level12.disabled = '';
	        } else {
	            $(levelElement2).html(getSecurityColor("For Official Use Only (FOUO)"));
	            level12.disabled = 'disabled';
	            if (distributionText.toLowerCase()=="statement a (public)") {
	                $(distributionElement2).html(getSecurityColor("Statement B"));
	                dist12.disabled = 'disabled';
	            }
	        }
		}
	}
	
	/* Return security level color for icon */
	function getSecurityColor(s) {
		switch(s) {
			case "Unclassified": 
			case "None":
			case "Statement A (Public)":
				$color = "green"; break;
			
			case "Confidential":
			case "Statement B":
			case "Statement C":
			case "Statement D":
				$color = "yellow"; break;
			
			case "Secret": 
			case "Top Secret": 
			case "For Official Use Only (FOUO)":
			case "Statement E":
			case "Statement F":
			case "Statement X":
				$color = "red"; break;
			
			default:
				return s; break;
		}
		$icon = "<span class='icon "+$color+"'></span>"+s;
		return $icon;
	}
	
	/* Expand or collapse sections */
	$("#more-section a.header").click(function() {
		$(this).parent().toggleClass("collapsed");
		return false;
	});
	
	
	/* ---- EPSS PROJECT screens ---- */
	
	/* Placeholder for Gagne Template sections and descriptions */
	var templateDescription=[
		"Present a stimulus that gains attention to ensure the learners are ready to learn and participate in activities. Methods for gaining learners' attention include stimulating students with novelty, uncertainty and surprise and posing thought-provoking questions.  You can also ask students to pose questions to be answered by other students.",
		"Inform students of the objectives or outcomes to help them understand what they are to learn during the lesson. You can state the outcomes by describing required performance, describing criteria for standard performance, or allowing the learner to establish criteria for standard performance.",
		"Help students by relating the new information to something they already know or something they have already experienced.  Methods for stimulating recall of prior knowledge include asking questions about previous experiences and their understanding of previous concepts.",
		"Use strategies to present and cue lesson content to provide more effective, efficient instruction.  A good approach is to organize and chunk content in a meaningful way. Additional ways to present and cue lesson content include: presenting vocabulary; providing examples; providing explanations after demonstrations; presenting multiple versions of the same content (e.g., video, demonstration, lecture, podcast, group work); and using a variety of media to address different learning preferences.",
		"Advise students of recommended strategies to aid learning and of resources available.  One approach is to provide instructional support as scaffolds (cues, hints, prompts) which can be removed after the student learns the task or content. Additional methods to provide learning guidance include: modeling varied learning strategies, mnemonics, concept mapping, role playing, and visualizing; using examples and non-examples to help students see what to do or not to do; and providing case studies, analogies, visual images, and metaphors.",
		"Activate student knowledge processing to help them internalize new skills and information and to confirm the correct understanding of these concepts. Ways to activate learner processing include:  eliciting student activities; asking deep-learning questions; making reference to what students already know; or have students collaborate with their peers. To elicit recall, you can ask students to receit, revisit, or reiterate information they have learned. You can facilitate student elaborations by asking them to explain details and provide more complexity to their responses.  New knowledge can be integrated with prior knowledge by using real-world examples.",
		"Provide immediate feedback of students' performance to assess and facilitate learning. Types of feedback include:  Confirmatory, Corrective, Remedial, Informative, and Analytical. Confirmatory feedback informs the student they did what he or she were supposed to do.  Corrective feedback informs the student of the accuracy of their performance or response.  Remedial feedback directs students in the right direction to find the correct answer, but does not provide the correct answer. Informative feedback provides information (new, different, additions, suggestions) to a student and confirms that you have been actively listening.  Analytical feedback provides the student with suggestions, recommendations, and information for them to correct their performance.",
		"In order to evaluate the effectiveness of the instructional events, you must test to see if the expected learning outcomes have been achieved. Performance should be based on previously stated objectives. Methods for testing learning include: pretesting for mastery of prerequisites or for endpoint knowledge or skills; post-testing to check for mastery of content or skills; embedding questions throughout instruction through oral questioning and/or quizzes; including objective or criterion-referenced performances which measure how well a student has learned a topic; and identifying normative-referenced performances which compare one student to another student.",
		"To help learners develop expertise, they must internalize new knowledge. Methods for helping learners internalize new knowledge include:  paraphrasing content; using metaphors; generating examples; creating concept maps or outlines; and creating job-aids, references, templates, or wizards."
		];
	/* Placeholder for Simulation Template sections and descriptions */
	var templateDescription2=[
	   	"Describe the broad topic and identify the important concepts to be covered in the simulation activity.",
	  	"Inform students of the performance objectives (or outcomes) to help them understand what they are to learn during the simulation activty. You can state the outcomes by describing required performance, describing criteria for standard performance, or allowing the learner to establish criteria for standard performance. You may also briefly describe the objectives for completing the simulation.",
	 	"Present a stimulus that motivates to ensure the learners are ready to learn and participate in activities. Methods for motivating learners include stimulating students with novelty, uncertainty and surprise and posing thought-provoking questions.  Motivation can also take the form of elements that are built into the design of the simulation, such as game-like elements of challenge, competition, or humor. You can relate the broad topic and major concepts of the simulation activity to real world experiences and needs while demonstrating the imporantance of the concepts and skills to be obtained.",
		"Describe the simulation activity in general terms and relate its use to the learner's organization or situational status. Provide examples of the simulated activity in real world situations.",
		"Provide context for the simulation activity the learner is about to experience. Discuss the roles included in the scenario, the rules and procedures to be enforced, the types of decisions each role may be asked to make, and the method of scoring. Relate these details to the goals and objectives of the simulation activity.  Describe for the learner how simulation objectives may be satisfied and how to know when they have been satisified within the simulation activity.",
		"Describe the scenario roles using increased detail and clarify the interactions between roles. Assign the learner's role and define the permissions, rules, and actions available to the role while in the simulation activity.",
		"Describe the navigation and activity controls available to the learner in the simulation environment. Demonstrate the behavior of these navigational and activity controls to illustrate expectations. This phase of instruction may also include interactive tutorials such as practice activities and/or interactive demos and self-running examples.",
		"Launch and administer the simulation environment. Track the learner's performance and react to learner decisions. Provide realistic effects to learner performance and decisions.",
		"Provide immediate feedback of students' performance to assess and facilitate learning. Types of feedback include:  Natural and Artificial. Natural feedback is provided by adjusting the simulation to reflect realistic outcomes based on learner performance and decisions.  Artifical feedback is presented to the learner using traditional mechanisms such as direct messaging.",
		"Provide remedial and reinforcement information based on the learner's performance.  For dynamic and variable simulations, this section may contain resources, descriptions or instructions intended for the activity programmer rather than the learner.  You may choose to describe misconception clarification methods, behaviors, or strategies from an instructional design perspective as guidance for the developer.",
		"Revisit the events and perceptions that took place within the simulation activity. Provide descriptions that relate the interventions to the goals and objectives for the training.  Review can be as simple as a checklist or as comprehensive as emotional sentiments and  cognitive choices. You may choose to describe event summary methods, behaviors, or strategies from an instructional design perspective as guidance for the developer.",
		"Provide a learner performance summary with guidance and insights where difficulties occurred. You may choose to describe performance summary methods, behaviors, or strategies from an instructional design perspective as guidance for the developer.",
		"Provide analysis of the overall simulation activity and the skills developed.",
		"Relate the situational learning to real world applications. Use examples and references to the learners duties and/or organization."
		];
	
	/* Click Project Title to edit */
	$('#projectTitleView').click(function() {
		$(this).slideUp('fast');
		var projectTitle = $('#projectTitleText').text();
		$('#projectTitleInput').val(projectTitle);
		$('#projectTitleEdit').slideDown('fast');
		$('#projectTitleInput').focus();
		$('#projectTitleInput').select();
		return false;
	});
	
	/* Done editing project title, apply changes when field loses focus */
	/* NOTE: would be nice to detect the ENTER key to also trigger input completion */
	$('#projectTitleInput').live('blur', function() {
		$('#projectTitleEdit').slideUp('fast');
		var projectTitle = $('#projectTitleInput').val();
		if (projectTitle==="") {
				projectTitle = $('#projectTitleText').text();
			}
		$('#epssUpdate').addClass('blue');
		$('#epssUpdate').removeClass('white');
		$('#r-save-alert').removeClass('hide');
		$('#projectTitleText').text(projectTitle);
		$('#projectTitleView').slideDown('fast');
		return false;
	});
	
	
	/* Remove asset object (delete) */
	$('.delete-asset').click(function() {
	    $(this).closest('.tile').fadeOut(250, function() {
			$(this).closest('td').remove();
		});
		return false;
	});
	
	/* Toggle Instructional strategy sections, update descriptions, show assets block */
	$('.templateSection').click(function() {
		var sectionIndex = $(this).index();
		if(selectedTemplateSection) {
			$(selectedTemplateSection).removeClass('active');
		} 
		selectedTemplateSection = $(this);
		var templateTitle = selectedTemplateSection.text();
		$(selectedTemplateSection).addClass('active');
		$('#findAssets').slideUp('fast');
		$('#projectDevNotes').slideUp('fast');
		$('#sectionAssets').slideUp('fast', function() {
			if(selectedAsset) {
				$(selectedAsset).removeClass('active');
				selectedAsset=null;
			}
			/* TO DO: Load sections asset here */
			$("#helptext-asset").text(templateDescription[sectionIndex]); /* update template section description */
			$.each($('span.template-title'), function()
			{
			   $(this).text(templateTitle); /* update section title wherever it appears */
			});
			$('#sectionAssets').slideDown('fast');
		});
		return false;
	});
	
	/* Toggle Instructional strategy sections, update descriptions, show assets block */
	$('.templateSection2').click(function() {
		var sectionIndex = $(this).index();
		if(selectedTemplateSection) {
			$(selectedTemplateSection).removeClass('active');
		} 
		selectedTemplateSection = $(this);
		var templateTitle = selectedTemplateSection.text();
		$(selectedTemplateSection).addClass('active');
		$('#findAssets').slideUp('fast');
		$('#projectDevNotes').slideUp('fast');
		$('#sectionAssets').slideUp('fast', function() {
			if(selectedAsset) {
				$(selectedAsset).removeClass('active');
				selectedAsset=null;
			}
			/* TO DO: Load sections asset here */
			$("#helptext-asset").text(templateDescription2[sectionIndex]); /* update template section description */
			$.each($('span.template-title'), function()
			{
			   $(this).text(templateTitle); /* update section title wherever it appears */
			});
			$('#sectionAssets').slideDown('fast');
		});
		return false;
	});
	
	/* Toggle asset selection, show dev notes input */
	$('.hotspot.notes').click(function() {
		if(selectedAsset) {
			$(selectedAsset).removeClass('active');
		}
		selectedAsset = $(this).prev('.cube');
		$(selectedAsset).addClass('active');
		$('#projectSectionNotes').slideUp('fast', function() {});
		$('#findAssets').slideUp('fast', function() {
			$('#projectDevNotes').slideUp('fast', function() {
				/* TO DO: load notes for the selected asset */
				//var tileAction = $(this);
				//assetTitle = "AssetName";
				//$.each($('span.asset-title'), function()
				//{
				//   $(this).text(assetTitle); /* update asset title wherever it appears */
				//});
				$('#projectDevNotes').slideDown('fast');
			});
		});
		return false;
	});
	
	/* Toggle asset selection, show dev notes input */
	$('.hotspot.sectionNotes').click(function() {
		if(selectedAsset) {
			$(selectedAsset).removeClass('active');
		}
		selectedAsset = $(this).prev('.cube');
		$(selectedAsset).addClass('active');
		$('#projectDevNotes').slideUp('fast', function() {});
		$('#findAssets').slideUp('fast', function() {
			$('#projectSectionNotes').slideUp('fast', function() {
				/* TO DO: load notes for the selected asset */
				var tileAction = $(this);
				assetTitle = "AssetName";
				$.each($('span.section-title'), function()
				{
				   $(this).text(assetTitle); /* update asset title wherever it appears */
				});
				$('#projectSectionNotes').slideDown('fast');
			});
		});
		return false;
	});
	
	
	/* Object Details: delete comment */
	$('#r-comments .delete').click(function() {
	    $(this).closest('.user-comment').fadeOut(250, function() {
			$(this).closest('td').remove();
		});
		return false;
	});
	
	/* Toggle 'find assets' options, show search results */
	$('.hotspot.search').click(function() {
		if(selectedAsset) {
			$(selectedAsset).removeClass('active');
		}
		selectedAsset = $(this).prev('.cube');
		$(selectedAsset).addClass('active');
		$('#projectDevNotes').slideUp('fast', function() {});
		$('#projectSectionNotes').slideUp('fast', function() {});
		$('#findAssets').slideUp('fast', function() {
			$('#projectDevNotes').slideUp('fast', function() {
				/* TO DO: load notes for the selected asset */
				var tileAction = $(this);
				$('#findAssets').slideDown('fast');
			});
		});
		return false;
	});
	
	/* Toggle suggested search terms off and on for the current section */
	$('.searchTerm').click(function() {
		$(this).toggleClass('blue'); /* Blue (default) = selected */
		$(this).toggleClass('white'); /* White = unselected */
	});
	
	$('#r-editSave').click(function() {
	                        $(this).addClass('white').removeClass('blue');
	                        $('#r-save-alert').addClass('hide');
	                       });
}