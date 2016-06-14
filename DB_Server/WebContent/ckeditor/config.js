/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	  
	config.removeDialogTabs = 'image:advanced;link:advanced';
	config.filebrowserBrowseUrl = '/lance/ckfinder/ckfinder.html';
	config.filebrowserImageBrowseUrl = '/lance/ckfinder/ckfinder.html?type=Images';
	config.filebrowserUploadUrl = '/lance/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=File';
	config.filebrowserImageUploadUrl = '/lance/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Images';
};
