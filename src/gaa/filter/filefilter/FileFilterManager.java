package gaa.filter.filefilter;

public class FileFilterManager {
	public static void main(String[] args) {
		RemoveFileFilter removeFileFilter  = new RemoveFileFilter();
		
		/*********************** REGRAS GERAIS ***********************/
		
		//F1
		removeFileFilter.filterAndPersistAllLanguages("%/docs/%");

		//F2
		removeFileFilter.filterAndPersistAllLanguages("examples/%");
		removeFileFilter.filterAndPersistAllLanguages("%/examples/%");

		//F3
		removeFileFilter.filterAndPersistByLanguage("Ruby", "lib/assets/javascripts/%");

		//F7
		removeFileFilter.filterAndPersistAllLanguages("build/%");

		//F9
		removeFileFilter.filterAndPersistAllLanguages("%/require.js");

		//F11
		removeFileFilter.filterAndPersistAllLanguages("benchmarks/%");
		
		//F49
		removeFileFilter.filterAndPersistByLanguage("PHP", "%/extlib/%");
		
		//F51
		removeFileFilter.filterAndPersistByLanguage("Python", "bin/%");


		/*********************** REGRAS ESPECIFICAS POR PROJETOS ***********************/
		
		
		//F14
		removeFileFilter.filterAndPersistByProject("mrdoob/three.js", "test/unit/qunit-1.10.0.js");
		
		//F15		
		removeFileFilter.filterAndPersistByProject("mrdoob/three.js", "test/unit/qunit-1.10.0.css");

		//F16		
		removeFileFilter.filterAndPersistByProject("mrdoob/three.js", "test/benchmark/benchmark-1.0.0.js");

		//F19		
		removeFileFilter.filterAndPersistByProject("mbostock/d3", "lib/%");

		//F22		
		removeFileFilter.filterAndPersistByProject("moment/moment", "min/%");

		//F24		
		removeFileFilter.filterAndPersistByProject("driftyco/ionic", "release/%");

		//F26		
		removeFileFilter.filterAndPersistByProject("Leaflet/Leaflet", "spec/expect.js");

		//F27		
		removeFileFilter.filterAndPersistByProject("Leaflet/Leaflet", "spec/sinon.js");
		
		//F31		
		removeFileFilter.filterAndPersistByProject("ajaxorg/ace", "api/resources/javascripts/jquery.collapse.js");

		//F32		
		removeFileFilter.filterAndPersistByProject("ajaxorg/ace", "api/resources/javascripts/jquery.cookie.js");
		
		//F35		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/ID3/%");

		//F36		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/SimplePie/%");

		//F37		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/Text/%");

		//F38		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/class-json.php");

		//F39		
		removeFileFilter.filterAndPersistByProject("mitchellh/vagrant", "website/www/source/javascripts/%");

		//F46		
		removeFileFilter.filterAndPersistByProject("jekyll/jekyll", "site/%");

		//F53		
		removeFileFilter.filterAndPersistByProject("django/django", "extras/%");

		//F54		
		removeFileFilter.filterAndPersistByProject("mailpile/Mailpile", "packages/%");

		//F55		
		removeFileFilter.filterAndPersistByProject("mailpile/Mailpile", "mailpile/contrib/%");

		//F59		
		removeFileFilter.filterAndPersistByProject("ipython/ipython", "IPython/html/static/%");

		//F60		
		removeFileFilter.filterAndPersistByProject("saltstack/salt", "conf/%");

		//F61		
		removeFileFilter.filterAndPersistByProject("saltstack/salt", "pkg/%");

		//F62		
		removeFileFilter.filterAndPersistByProject("getsentry/sentry", "hooks/%");

		//F63		
		removeFileFilter.filterAndPersistByProject("getsentry/sentry", "src/sentry/static/sentry/scripts/lib/%");

		//F64		
		removeFileFilter.filterAndPersistByProject("Eugeny/ajenti", "packaging/%");

		//F67		
		removeFileFilter.filterAndPersistByProject("ansible/ansible", "docsite/%");

		//F68		
		removeFileFilter.filterAndPersistByProject("ansible/ansible", "plugins/%");

		//F70		
		removeFileFilter.filterAndPersistByProject("odoo/odoo", "setup/%");

		//F72		
		removeFileFilter.filterAndPersistByProject("kivy/kivy", "kivy/lib/%");

		//F74		
		removeFileFilter.filterAndPersistByProject("chef/chef", "distro/common/html/_static/%");
		
		//F77		
		removeFileFilter.filterAndPersistByProject("rails/rails", "actionview/test/fixtures/public/%");

		//F78		
		removeFileFilter.filterAndPersistByProject("rails/rails", "guides/%");

		//F80		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "readme.html");

		//F81		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/crop/%");

		//F82		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/imgareaselect/%");
		
		//F84		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/jquery/%");

		//F85		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/media/%");

		//F86		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/mediaelement/%");

		//F87		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/plupload/%");

		//F88		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/swfupload/%");

		//F90		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/thickbox/%");

		//F91		
		removeFileFilter.filterAndPersistByProject("WordPress/WordPress", "wp-includes/js/tinymce/%");

		//F101		
		removeFileFilter.filterAndPersistByProject("joomla/joomla-cms", "tmp/%");

		//F102		
		removeFileFilter.filterAndPersistByProject("joomla/joomla-cms", "tests/system/webdriver/SeleniumClient/%");

		//F105		
		removeFileFilter.filterAndPersistByProject("joomla/joomla-cms", "media/%");

		//F106		
		removeFileFilter.filterAndPersistByProject("joomla/joomla-cms", "images/%");

		//F113		
		removeFileFilter.filterAndPersistByProject("ThinkUpLLC/ThinkUp", "webapp/assets/js/jqBootstrapValidation.js");

	
		/****************** EXCECOES A SEREM TRATADAS INDIVIDUALMENTE *******************/
		
		
		//F39 e F$)		
		removeFileFilter.removeEspecifcfilterAndPersistByProject("mitchellh/vagrant", "%vagrantup.js");
	}
}
