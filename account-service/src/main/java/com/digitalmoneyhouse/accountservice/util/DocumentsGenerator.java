package com.digitalmoneyhouse.accountservice.util;

import com.digitalmoneyhouse.accountservice.dto.DepositResponse;
import com.digitalmoneyhouse.accountservice.dto.DocumentContainer;
import com.digitalmoneyhouse.accountservice.dto.TransactionResponse;
import com.digitalmoneyhouse.accountservice.dto.TransferenceResponse;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.model.Account;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.layout.font.FontProvider;
import com.opencsv.CSVWriter;
import com.spire.doc.*;
import com.spire.doc.Document;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Component
public class DocumentsGenerator {

    private final Character CSV_SEPARATOR = ';';
    private final String FONT_FAMILY = "Open Sans";

    public DocumentContainer generateReceipt(TransactionResponse transactionResponse) throws IOException {
        String fileName = transactionResponse.getTransactionCode() + ".pdf";
        String html = "";
        if (transactionResponse instanceof TransferenceResponse) {
            TransferenceResponse transferenceResponse = (TransferenceResponse) transactionResponse;
            html = generateTransferenceHtml(transferenceResponse);
        } else if (transactionResponse instanceof DepositResponse) {
            DepositResponse depositResponse = (DepositResponse) transactionResponse;
            html = generateDepositHtml(depositResponse);
        }

        InputStream openSansVariableInputStream = this.getClass().getClassLoader()
                .getResourceAsStream("fonts/OpenSans-VariableFont_wdth,wght.ttf");
        InputStream openSansBoldInputStream = this.getClass().getClassLoader()
                .getResourceAsStream("fonts/OpenSans-Bold.ttf");

        FontProgram openSansVariableFontProgram = FontProgramFactory.createFont(openSansVariableInputStream.readAllBytes());
        FontProgram openSansBoldFontProgram = FontProgramFactory.createFont(openSansBoldInputStream.readAllBytes());
        FontProvider fontProvider = new DefaultFontProvider();
        fontProvider.addFont(openSansVariableFontProgram, PdfEncodings.IDENTITY_H);
        fontProvider.addFont(openSansBoldFontProgram, PdfEncodings.IDENTITY_H);
        ConverterProperties properties = new ConverterProperties();
        properties.setFontProvider(fontProvider);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument =  new PdfDocument(new PdfWriter(byteArrayOutputStream));
        PageSize pageSize = new PageSize(525, 700);
        pdfDocument.setDefaultPageSize(pageSize);
        pageSize.applyMargins(0, 0, 0, 0, true);
        HtmlConverter.convertToPdf(html, pdfDocument, properties);
        return new DocumentContainer(byteArrayOutputStream.toByteArray(), fileName);
    }

    private String generateTransferenceHtml(TransferenceResponse transferenceResponse) {
        String html = "<div style=\"position: absolute; width: 602px; height: 819px; left: 0px; background: #201F22;\">\n" +
                "\t\t<div style=\"position: absolute; width: 602px; height: 84px; background: #C1FD35;\">\n" +
                "\t\t\t<svg style=\"position: absolute; width: 344px; height: 38px; left: 123px; top: 23px;\" width=\"344\" height=\"38\" viewBox=\"0 0 344 38\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<path d=\"M0 10C0 4.47715 4.47715 0 10 0H121V38H10C4.47715 38 0 33.5228 0 28V10Z\" fill=\"#C1FD35\"/>\n" +
                "\t\t\t\t<path d=\"M344 28C344 33.5228 339.523 38 334 38L121 38L121 -4.21793e-07L334 1.81993e-05C339.523 1.86821e-05 344 4.47717 344 10L344 28Z\" fill=\"#201F22\"/>\n" +
                "\t\t\t\t<path d=\"M15.5107 30H10.8232L10.8525 26.3086H15.5107C16.5752 26.3086 17.4736 26.0645 18.2061 25.5762C18.9385 25.0781 19.4951 24.3457 19.876 23.3789C20.2568 22.4023 20.4473 21.2158 20.4473 19.8193V18.8379C20.4473 17.7832 20.3398 16.8555 20.125 16.0547C19.9102 15.2441 19.5928 14.5654 19.1729 14.0186C18.7529 13.4717 18.2305 13.0615 17.6055 12.7881C16.9902 12.5146 16.2822 12.3779 15.4814 12.3779H10.7354V8.67188H15.4814C16.9268 8.67188 18.2451 8.91602 19.4365 9.4043C20.6377 9.89258 21.6729 10.5908 22.542 11.499C23.4209 12.4072 24.0996 13.4814 24.5781 14.7217C25.0566 15.9619 25.2959 17.3438 25.2959 18.8672V19.8193C25.2959 21.333 25.0566 22.7148 24.5781 23.9648C24.0996 25.2051 23.4209 26.2793 22.542 27.1875C21.6729 28.0859 20.6426 28.7793 19.4512 29.2676C18.2598 29.7559 16.9463 30 15.5107 30ZM13.5332 8.67188V30H8.75781V8.67188H13.5332ZM33.2939 8.67188V30H28.5332V8.67188H33.2939ZM54.3291 18.7354V27.334C53.9775 27.7344 53.4404 28.1641 52.7178 28.623C52.0049 29.082 51.0918 29.4775 49.9785 29.8096C48.8652 30.1318 47.5371 30.293 45.9941 30.293C44.5977 30.293 43.3281 30.0684 42.1855 29.6191C41.043 29.1602 40.0615 28.4912 39.2412 27.6123C38.4209 26.7236 37.7861 25.6396 37.3369 24.3604C36.8975 23.0811 36.6777 21.6162 36.6777 19.9658V18.7061C36.6777 17.0557 36.8975 15.5908 37.3369 14.3115C37.7764 13.0322 38.3965 11.9531 39.1973 11.0742C39.998 10.1855 40.9502 9.51172 42.0537 9.05273C43.1572 8.59375 44.3779 8.36426 45.7158 8.36426C47.6201 8.36426 49.1777 8.67188 50.3887 9.28711C51.5996 9.89258 52.5176 10.7324 53.1426 11.8066C53.7773 12.8809 54.168 14.1162 54.3145 15.5127H49.7148C49.6172 14.7998 49.4219 14.1846 49.1289 13.667C48.8457 13.1494 48.4404 12.7539 47.9131 12.4805C47.3857 12.1973 46.7021 12.0557 45.8623 12.0557C45.1885 12.0557 44.5781 12.1973 44.0312 12.4805C43.4941 12.7539 43.04 13.1689 42.6689 13.7256C42.2979 14.2822 42.0146 14.9756 41.8193 15.8057C41.624 16.626 41.5264 17.583 41.5264 18.6768V19.9658C41.5264 21.0596 41.624 22.0215 41.8193 22.8516C42.0244 23.6816 42.3223 24.375 42.7129 24.9316C43.1035 25.4785 43.5967 25.8936 44.1924 26.1768C44.7979 26.46 45.501 26.6016 46.3018 26.6016C46.9268 26.6016 47.4541 26.5527 47.8838 26.4551C48.3135 26.3477 48.665 26.2158 48.9385 26.0596C49.2119 25.8936 49.417 25.7422 49.5537 25.6055V22.0605H45.7598V18.7354H54.3291ZM62.6787 8.67188V30H57.918V8.67188H62.6787ZM76.0234 8.67188V30H71.2627V8.67188H76.0234ZM82.5127 8.67188V12.3779H64.9053V8.67188H82.5127ZM92.0635 12.7295L86.5557 30H81.458L89.3535 8.67188H92.5762L92.0635 12.7295ZM96.6338 30L91.1113 12.7295L90.54 8.67188H93.8066L101.731 30H96.6338ZM96.3994 22.0605V25.752H85.2812V22.0605H96.3994ZM117.42 26.3086V30H106.653V26.3086H117.42ZM108.279 8.67188V30H103.504V8.67188H108.279Z\" fill=\"#201F22\"/>\n" +
                "\t\t\t\t<path d=\"M130.662 8.67188H133.943L140.11 25.1221L146.263 8.67188H149.544L141.399 30H138.792L130.662 8.67188ZM129.168 8.67188H132.288L132.83 22.9102V30H129.168V8.67188ZM147.918 8.67188H151.053V30H147.376V22.9102L147.918 8.67188ZM172.425 18.75V19.9219C172.425 21.5332 172.215 22.9785 171.795 24.2578C171.375 25.5371 170.774 26.626 169.993 27.5244C169.222 28.4229 168.294 29.1113 167.21 29.5898C166.126 30.0586 164.925 30.293 163.606 30.293C162.298 30.293 161.102 30.0586 160.018 29.5898C158.943 29.1113 158.011 28.4229 157.22 27.5244C156.429 26.626 155.813 25.5371 155.374 24.2578C154.944 22.9785 154.729 21.5332 154.729 19.9219V18.75C154.729 17.1387 154.944 15.6982 155.374 14.4287C155.804 13.1494 156.409 12.0605 157.19 11.1621C157.981 10.2539 158.914 9.56543 159.988 9.09668C161.072 8.61816 162.269 8.37891 163.577 8.37891C164.896 8.37891 166.097 8.61816 167.181 9.09668C168.265 9.56543 169.197 10.2539 169.979 11.1621C170.76 12.0605 171.36 13.1494 171.78 14.4287C172.21 15.6982 172.425 17.1387 172.425 18.75ZM168.748 19.9219V18.7207C168.748 17.5293 168.631 16.4795 168.396 15.5713C168.172 14.6533 167.835 13.8867 167.386 13.2715C166.946 12.6465 166.404 12.1777 165.76 11.8652C165.115 11.543 164.388 11.3818 163.577 11.3818C162.767 11.3818 162.044 11.543 161.409 11.8652C160.774 12.1777 160.232 12.6465 159.783 13.2715C159.344 13.8867 159.007 14.6533 158.772 15.5713C158.538 16.4795 158.421 17.5293 158.421 18.7207V19.9219C158.421 21.1133 158.538 22.168 158.772 23.0859C159.007 24.0039 159.349 24.7803 159.798 25.415C160.257 26.04 160.804 26.5137 161.438 26.8359C162.073 27.1484 162.796 27.3047 163.606 27.3047C164.427 27.3047 165.154 27.1484 165.789 26.8359C166.424 26.5137 166.961 26.04 167.4 25.415C167.84 24.7803 168.172 24.0039 168.396 23.0859C168.631 22.168 168.748 21.1133 168.748 19.9219ZM193.021 8.67188V30H189.344L179.778 14.7217V30H176.102V8.67188H179.778L189.373 23.9795V8.67188H193.021ZM211.36 27.085V30H200.037V27.085H211.36ZM201.077 8.67188V30H197.4V8.67188H201.077ZM209.881 17.5781V20.4492H200.037V17.5781H209.881ZM211.287 8.67188V11.6016H200.037V8.67188H211.287ZM216.399 8.67188L221.351 18.8525L226.302 8.67188H230.374L223.196 22.1484V30H219.49V22.1484L212.312 8.67188H216.399ZM254.266 17.5781V20.4932H242.942V17.5781H254.266ZM243.851 8.67188V30H240.174V8.67188H243.851ZM257.093 8.67188V30H253.431V8.67188H257.093ZM278.479 18.75V19.9219C278.479 21.5332 278.27 22.9785 277.85 24.2578C277.43 25.5371 276.829 26.626 276.048 27.5244C275.276 28.4229 274.349 29.1113 273.265 29.5898C272.181 30.0586 270.979 30.293 269.661 30.293C268.353 30.293 267.156 30.0586 266.072 29.5898C264.998 29.1113 264.065 28.4229 263.274 27.5244C262.483 26.626 261.868 25.5371 261.429 24.2578C260.999 22.9785 260.784 21.5332 260.784 19.9219V18.75C260.784 17.1387 260.999 15.6982 261.429 14.4287C261.858 13.1494 262.464 12.0605 263.245 11.1621C264.036 10.2539 264.969 9.56543 266.043 9.09668C267.127 8.61816 268.323 8.37891 269.632 8.37891C270.95 8.37891 272.151 8.61816 273.235 9.09668C274.319 9.56543 275.252 10.2539 276.033 11.1621C276.814 12.0605 277.415 13.1494 277.835 14.4287C278.265 15.6982 278.479 17.1387 278.479 18.75ZM274.803 19.9219V18.7207C274.803 17.5293 274.686 16.4795 274.451 15.5713C274.227 14.6533 273.89 13.8867 273.44 13.2715C273.001 12.6465 272.459 12.1777 271.814 11.8652C271.17 11.543 270.442 11.3818 269.632 11.3818C268.821 11.3818 268.099 11.543 267.464 11.8652C266.829 12.1777 266.287 12.6465 265.838 13.2715C265.398 13.8867 265.062 14.6533 264.827 15.5713C264.593 16.4795 264.476 17.5293 264.476 18.7207V19.9219C264.476 21.1133 264.593 22.168 264.827 23.0859C265.062 24.0039 265.403 24.7803 265.853 25.415C266.312 26.04 266.858 26.5137 267.493 26.8359C268.128 27.1484 268.851 27.3047 269.661 27.3047C270.481 27.3047 271.209 27.1484 271.844 26.8359C272.479 26.5137 273.016 26.04 273.455 25.415C273.895 24.7803 274.227 24.0039 274.451 23.0859C274.686 22.168 274.803 21.1133 274.803 19.9219ZM294.124 8.67188H297.786V22.9248C297.786 24.5459 297.435 25.9033 296.731 26.9971C296.028 28.0908 295.071 28.916 293.86 29.4727C292.659 30.0195 291.316 30.293 289.832 30.293C288.299 30.293 286.932 30.0195 285.73 29.4727C284.529 28.916 283.582 28.0908 282.889 26.9971C282.205 25.9033 281.863 24.5459 281.863 22.9248V8.67188H285.525V22.9248C285.525 23.9502 285.701 24.7949 286.053 25.459C286.404 26.1133 286.902 26.5967 287.547 26.9092C288.191 27.2217 288.953 27.3779 289.832 27.3779C290.711 27.3779 291.468 27.2217 292.103 26.9092C292.747 26.5967 293.245 26.1133 293.597 25.459C293.948 24.7949 294.124 23.9502 294.124 22.9248V8.67188ZM313.006 24.5068C313.006 24.0674 312.938 23.6768 312.801 23.335C312.674 22.9932 312.444 22.6807 312.112 22.3975C311.78 22.1143 311.312 21.8408 310.706 21.5771C310.11 21.3037 309.349 21.0254 308.421 20.7422C307.405 20.4297 306.468 20.083 305.608 19.7021C304.759 19.3115 304.017 18.8623 303.382 18.3545C302.747 17.8369 302.254 17.2461 301.902 16.582C301.551 15.9082 301.375 15.1318 301.375 14.2529C301.375 13.3838 301.556 12.5928 301.917 11.8799C302.288 11.167 302.811 10.5518 303.484 10.0342C304.168 9.50684 304.974 9.10156 305.901 8.81836C306.829 8.52539 307.854 8.37891 308.978 8.37891C310.56 8.37891 311.922 8.67188 313.064 9.25781C314.217 9.84375 315.101 10.6299 315.716 11.6162C316.341 12.6025 316.653 13.6914 316.653 14.8828H313.006C313.006 14.1797 312.854 13.5596 312.552 13.0225C312.259 12.4756 311.81 12.0459 311.204 11.7334C310.608 11.4209 309.852 11.2646 308.934 11.2646C308.064 11.2646 307.342 11.3965 306.766 11.6602C306.189 11.9238 305.76 12.2803 305.477 12.7295C305.193 13.1787 305.052 13.6865 305.052 14.2529C305.052 14.6533 305.145 15.0195 305.33 15.3516C305.516 15.6738 305.799 15.9766 306.18 16.2598C306.561 16.5332 307.039 16.792 307.615 17.0361C308.191 17.2803 308.87 17.5146 309.651 17.7393C310.833 18.0908 311.863 18.4814 312.742 18.9111C313.621 19.3311 314.354 19.8096 314.939 20.3467C315.525 20.8838 315.965 21.4941 316.258 22.1777C316.551 22.8516 316.697 23.6182 316.697 24.4775C316.697 25.376 316.517 26.1865 316.155 26.9092C315.794 27.6221 315.276 28.2324 314.603 28.7402C313.938 29.2383 313.138 29.624 312.2 29.8975C311.272 30.1611 310.237 30.293 309.095 30.293C308.069 30.293 307.059 30.1562 306.062 29.8828C305.076 29.6094 304.178 29.1943 303.367 28.6377C302.557 28.0713 301.912 27.3682 301.434 26.5283C300.955 25.6787 300.716 24.6875 300.716 23.5547H304.393C304.393 24.248 304.51 24.8389 304.744 25.3271C304.988 25.8154 305.325 26.2158 305.755 26.5283C306.185 26.8311 306.683 27.0557 307.249 27.2021C307.825 27.3486 308.44 27.4219 309.095 27.4219C309.954 27.4219 310.672 27.2998 311.248 27.0557C311.834 26.8115 312.273 26.4697 312.566 26.0303C312.859 25.5908 313.006 25.083 313.006 24.5068ZM333.909 27.085V30H322.586V27.085H333.909ZM323.626 8.67188V30H319.949V8.67188H323.626ZM332.43 17.5781V20.4492H322.586V17.5781H332.43ZM333.836 8.67188V11.6016H322.586V8.67188H333.836Z\" fill=\"white\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t</div>\n" +
                "\t\t<p style=\"position: absolute; width: 368px; height: 33px; left: 45px; top: 112px; font-family: 'Open Sans'; font-style: normal; font-weight: 700; font-size: 24px; line-height: 33px; display: flex; align-items: flex-end; text-align: center; color: #C1FD35;\">Comprovante de transferência</p>\n" +
                "\t\t<p style=\"position: absolute; width: 235px; height: 22px; left: 50px; top: 149px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; text-align: center; color: #EEEAEA;\">TRANSACTION_DATE_TIME</p>\n" +
                "\t\t<div style=\"position: absolute; width: 514px; height: 573px; left: 44px; top: 200px; border-radius: 10px; background: #FFFFFF;\">\n" +
                "\t\t\t<p style=\"position: absolute; width: 103px; height: 22px; left: 30px; top: 21px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Transferência</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 33px; left: 30px; top: 30px; font-family: 'Open Sans'; font-style: normal; font-weight: 700; font-size: 24px; line-height: 33px; display: flex; align-items: flex-end; text-align: left; color: #201F22;\">R$ TRANSACTION_AMOUNT</p>\n" +
                "\t\t\t<svg style=\"position: absolute; width: 453px; height: 0px; left: 30px; top: 89px; border: 1px solid #CECECE;\" width=\"453\" height=\"1\" viewBox=\"0 0 453 1\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<line y1=\"0.5\" x2=\"453\" y2=\"0.5\" stroke=\"#CECECE\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<p style=\"position: absolute; left: 58px; top: 94px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">De</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 33px; left: 58px; top: 109px; font-family: 'Open Sans'; font-style: normal; font-weight: 700; font-size: 24px; line-height: 33px; display: flex; align-items: flex-end; color: #201F22;\">ORIGIN_ACCOUNT_HOLDER_NAME</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 242px; height: 22px; left: 58px; top: 150px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Conta:</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 242px; height: 22px; left: 118px; top: 150px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end;\">ORIGIN_ACCOUNT_NUMBER</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 203px; height: 22px; left: 58px; top: 178px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: #201F22;\">Conta Digital Money House</p>\n" +
                "\n" +
                "\t\t\t<p style=\"position: absolute; left: 58px; top: 258px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Para</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 33px; left: 58px; top: 272px; font-family: 'Open Sans'; font-style: normal; font-weight: 700; font-size: 24px; line-height: 33px; display: flex; align-items: flex-end; color: #201F22;\">DESTINATION_ACCOUNT_HOLDER_NAME</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 242px; height: 22px; left: 58px; top: 313px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Conta:</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 242px; height: 22px; left: 118px; top: 313px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end;\">DESTINATION_ACCOUNT_NUMBER</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 203px; height: 22px; left: 58px; top: 341px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: #201F22;\">Conta Digital Money House</p>\n" +
                "\t\t\t<svg style=\"position: absolute; left: 33px; top: 119px; border: 1px solid #CECECE;\" width=\"1\" height=\"164\" viewBox=\"0 0 1 164\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<line x1=\"0.5\" y1=\"2.18557e-08\" x2=\"0.499993\" y2=\"164\" stroke=\"#CECECE\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<svg style=\"position: absolute; width: 7px; height: 7px; border-radius: 50%; left: 30px; top: 115px; background: #3A393E; z-index: 2;\" width=\"7\" height=\"7\" viewBox=\"0 0 7 7\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<circle cx=\"3.5\" cy=\"3.5\" r=\"3.5\" fill=\"#3A393E\"/>\n" +
                "\t\t\t</svg>\t\t\t\t\n" +
                "\t\t\t<svg style=\"position: absolute; width: 7px; height: 7px; border-radius: 50%; left: 30px; top: 279px; background: #3A393E; z-index: 2;\" width=\"7\" height=\"7\" viewBox=\"0 0 7 7\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<circle cx=\"3.5\" cy=\"3.5\" r=\"3.5\" fill=\"#3A393E\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<svg style=\"position: absolute; width: 453px; height: 0px; left: 30px; top: 431px; border: 1px solid #CECECE;\" width=\"453\" height=\"1\" viewBox=\"0 0 453 1\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<line y1=\"0.5\" x2=\"453\" y2=\"0.5\" stroke=\"#CECECE\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<p style=\"position: absolute; width: 105px; height: 22px; left: 58px; top: 432px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Motivo:</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 22px; left: 118px; top: 432px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end;\">DESCRIPTION</p>\n" +
                "\t\t\t<svg style=\"position: absolute; width: 453px; height: 0px; left: 30px; top: 490px; border: 1px solid #CECECE;\" width=\"453\" height=\"1\" viewBox=\"0 0 453 1\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<line y1=\"0.5\" x2=\"453\" y2=\"0.5\" stroke=\"#CECECE\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<p style=\"position: absolute; width: 178px; height: 22px; left: 58px; top: 488px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Código de transferência</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 22px; left: 58px; top: 516px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: #201F22;\">TRANSACTION_CODE</p>\n" +
                "\t\t\t\t\t\t\t\n" +
                "\t\t</div>\n" +
                "\t</div>";

        String transactionDateTime = Formatter.formatDateTime(transferenceResponse.getDate());
        String transferenceAmount = Formatter.formatDouble(transferenceResponse.getAmount());
        String originAccountHolderName = transferenceResponse.getOriginAccountHolderName();
        String originAccountNumber = transferenceResponse.getOriginAccountNumber();
        String detinationAccountHolderName = transferenceResponse.getDestinationAccountHolderName();
        String destinationAccountNumber = transferenceResponse.getDestinationAccountNumber();
        String description = transferenceResponse.getDescription();
        String transferenceCode = transferenceResponse.getTransactionCode();

        html = html
                .replace("TRANSACTION_DATE_TIME", transactionDateTime)
                .replace("TRANSACTION_AMOUNT", transferenceAmount)
                .replace("ORIGIN_ACCOUNT_HOLDER_NAME", originAccountHolderName)
                .replace("ORIGIN_ACCOUNT_NUMBER", originAccountNumber)
                .replace("DESTINATION_ACCOUNT_HOLDER_NAME", detinationAccountHolderName)
                .replace("DESTINATION_ACCOUNT_NUMBER", destinationAccountNumber)
                .replace("DESCRIPTION", (description != null) ? description : "")
                .replace("TRANSACTION_CODE", transferenceCode);

        return html;
    }

    private String generateDepositHtml(DepositResponse depositResponse) {
        String html = "<div style=\"position: absolute; width: 602px; height: 819px; left: 0px; background: #201F22;\">\n" +
                "\t\t<div style=\"position: absolute; width: 602px; height: 84px; background: #C1FD35;\">\n" +
                "\t\t\t<svg style=\"position: absolute; width: 344px; height: 38px; left: 123px; top: 23px;\" width=\"344\" height=\"38\" viewBox=\"0 0 344 38\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<path d=\"M0 10C0 4.47715 4.47715 0 10 0H121V38H10C4.47715 38 0 33.5228 0 28V10Z\" fill=\"#C1FD35\"/>\n" +
                "\t\t\t\t<path d=\"M344 28C344 33.5228 339.523 38 334 38L121 38L121 -4.21793e-07L334 1.81993e-05C339.523 1.86821e-05 344 4.47717 344 10L344 28Z\" fill=\"#201F22\"/>\n" +
                "\t\t\t\t<path d=\"M15.5107 30H10.8232L10.8525 26.3086H15.5107C16.5752 26.3086 17.4736 26.0645 18.2061 25.5762C18.9385 25.0781 19.4951 24.3457 19.876 23.3789C20.2568 22.4023 20.4473 21.2158 20.4473 19.8193V18.8379C20.4473 17.7832 20.3398 16.8555 20.125 16.0547C19.9102 15.2441 19.5928 14.5654 19.1729 14.0186C18.7529 13.4717 18.2305 13.0615 17.6055 12.7881C16.9902 12.5146 16.2822 12.3779 15.4814 12.3779H10.7354V8.67188H15.4814C16.9268 8.67188 18.2451 8.91602 19.4365 9.4043C20.6377 9.89258 21.6729 10.5908 22.542 11.499C23.4209 12.4072 24.0996 13.4814 24.5781 14.7217C25.0566 15.9619 25.2959 17.3438 25.2959 18.8672V19.8193C25.2959 21.333 25.0566 22.7148 24.5781 23.9648C24.0996 25.2051 23.4209 26.2793 22.542 27.1875C21.6729 28.0859 20.6426 28.7793 19.4512 29.2676C18.2598 29.7559 16.9463 30 15.5107 30ZM13.5332 8.67188V30H8.75781V8.67188H13.5332ZM33.2939 8.67188V30H28.5332V8.67188H33.2939ZM54.3291 18.7354V27.334C53.9775 27.7344 53.4404 28.1641 52.7178 28.623C52.0049 29.082 51.0918 29.4775 49.9785 29.8096C48.8652 30.1318 47.5371 30.293 45.9941 30.293C44.5977 30.293 43.3281 30.0684 42.1855 29.6191C41.043 29.1602 40.0615 28.4912 39.2412 27.6123C38.4209 26.7236 37.7861 25.6396 37.3369 24.3604C36.8975 23.0811 36.6777 21.6162 36.6777 19.9658V18.7061C36.6777 17.0557 36.8975 15.5908 37.3369 14.3115C37.7764 13.0322 38.3965 11.9531 39.1973 11.0742C39.998 10.1855 40.9502 9.51172 42.0537 9.05273C43.1572 8.59375 44.3779 8.36426 45.7158 8.36426C47.6201 8.36426 49.1777 8.67188 50.3887 9.28711C51.5996 9.89258 52.5176 10.7324 53.1426 11.8066C53.7773 12.8809 54.168 14.1162 54.3145 15.5127H49.7148C49.6172 14.7998 49.4219 14.1846 49.1289 13.667C48.8457 13.1494 48.4404 12.7539 47.9131 12.4805C47.3857 12.1973 46.7021 12.0557 45.8623 12.0557C45.1885 12.0557 44.5781 12.1973 44.0312 12.4805C43.4941 12.7539 43.04 13.1689 42.6689 13.7256C42.2979 14.2822 42.0146 14.9756 41.8193 15.8057C41.624 16.626 41.5264 17.583 41.5264 18.6768V19.9658C41.5264 21.0596 41.624 22.0215 41.8193 22.8516C42.0244 23.6816 42.3223 24.375 42.7129 24.9316C43.1035 25.4785 43.5967 25.8936 44.1924 26.1768C44.7979 26.46 45.501 26.6016 46.3018 26.6016C46.9268 26.6016 47.4541 26.5527 47.8838 26.4551C48.3135 26.3477 48.665 26.2158 48.9385 26.0596C49.2119 25.8936 49.417 25.7422 49.5537 25.6055V22.0605H45.7598V18.7354H54.3291ZM62.6787 8.67188V30H57.918V8.67188H62.6787ZM76.0234 8.67188V30H71.2627V8.67188H76.0234ZM82.5127 8.67188V12.3779H64.9053V8.67188H82.5127ZM92.0635 12.7295L86.5557 30H81.458L89.3535 8.67188H92.5762L92.0635 12.7295ZM96.6338 30L91.1113 12.7295L90.54 8.67188H93.8066L101.731 30H96.6338ZM96.3994 22.0605V25.752H85.2812V22.0605H96.3994ZM117.42 26.3086V30H106.653V26.3086H117.42ZM108.279 8.67188V30H103.504V8.67188H108.279Z\" fill=\"#201F22\"/>\n" +
                "\t\t\t\t<path d=\"M130.662 8.67188H133.943L140.11 25.1221L146.263 8.67188H149.544L141.399 30H138.792L130.662 8.67188ZM129.168 8.67188H132.288L132.83 22.9102V30H129.168V8.67188ZM147.918 8.67188H151.053V30H147.376V22.9102L147.918 8.67188ZM172.425 18.75V19.9219C172.425 21.5332 172.215 22.9785 171.795 24.2578C171.375 25.5371 170.774 26.626 169.993 27.5244C169.222 28.4229 168.294 29.1113 167.21 29.5898C166.126 30.0586 164.925 30.293 163.606 30.293C162.298 30.293 161.102 30.0586 160.018 29.5898C158.943 29.1113 158.011 28.4229 157.22 27.5244C156.429 26.626 155.813 25.5371 155.374 24.2578C154.944 22.9785 154.729 21.5332 154.729 19.9219V18.75C154.729 17.1387 154.944 15.6982 155.374 14.4287C155.804 13.1494 156.409 12.0605 157.19 11.1621C157.981 10.2539 158.914 9.56543 159.988 9.09668C161.072 8.61816 162.269 8.37891 163.577 8.37891C164.896 8.37891 166.097 8.61816 167.181 9.09668C168.265 9.56543 169.197 10.2539 169.979 11.1621C170.76 12.0605 171.36 13.1494 171.78 14.4287C172.21 15.6982 172.425 17.1387 172.425 18.75ZM168.748 19.9219V18.7207C168.748 17.5293 168.631 16.4795 168.396 15.5713C168.172 14.6533 167.835 13.8867 167.386 13.2715C166.946 12.6465 166.404 12.1777 165.76 11.8652C165.115 11.543 164.388 11.3818 163.577 11.3818C162.767 11.3818 162.044 11.543 161.409 11.8652C160.774 12.1777 160.232 12.6465 159.783 13.2715C159.344 13.8867 159.007 14.6533 158.772 15.5713C158.538 16.4795 158.421 17.5293 158.421 18.7207V19.9219C158.421 21.1133 158.538 22.168 158.772 23.0859C159.007 24.0039 159.349 24.7803 159.798 25.415C160.257 26.04 160.804 26.5137 161.438 26.8359C162.073 27.1484 162.796 27.3047 163.606 27.3047C164.427 27.3047 165.154 27.1484 165.789 26.8359C166.424 26.5137 166.961 26.04 167.4 25.415C167.84 24.7803 168.172 24.0039 168.396 23.0859C168.631 22.168 168.748 21.1133 168.748 19.9219ZM193.021 8.67188V30H189.344L179.778 14.7217V30H176.102V8.67188H179.778L189.373 23.9795V8.67188H193.021ZM211.36 27.085V30H200.037V27.085H211.36ZM201.077 8.67188V30H197.4V8.67188H201.077ZM209.881 17.5781V20.4492H200.037V17.5781H209.881ZM211.287 8.67188V11.6016H200.037V8.67188H211.287ZM216.399 8.67188L221.351 18.8525L226.302 8.67188H230.374L223.196 22.1484V30H219.49V22.1484L212.312 8.67188H216.399ZM254.266 17.5781V20.4932H242.942V17.5781H254.266ZM243.851 8.67188V30H240.174V8.67188H243.851ZM257.093 8.67188V30H253.431V8.67188H257.093ZM278.479 18.75V19.9219C278.479 21.5332 278.27 22.9785 277.85 24.2578C277.43 25.5371 276.829 26.626 276.048 27.5244C275.276 28.4229 274.349 29.1113 273.265 29.5898C272.181 30.0586 270.979 30.293 269.661 30.293C268.353 30.293 267.156 30.0586 266.072 29.5898C264.998 29.1113 264.065 28.4229 263.274 27.5244C262.483 26.626 261.868 25.5371 261.429 24.2578C260.999 22.9785 260.784 21.5332 260.784 19.9219V18.75C260.784 17.1387 260.999 15.6982 261.429 14.4287C261.858 13.1494 262.464 12.0605 263.245 11.1621C264.036 10.2539 264.969 9.56543 266.043 9.09668C267.127 8.61816 268.323 8.37891 269.632 8.37891C270.95 8.37891 272.151 8.61816 273.235 9.09668C274.319 9.56543 275.252 10.2539 276.033 11.1621C276.814 12.0605 277.415 13.1494 277.835 14.4287C278.265 15.6982 278.479 17.1387 278.479 18.75ZM274.803 19.9219V18.7207C274.803 17.5293 274.686 16.4795 274.451 15.5713C274.227 14.6533 273.89 13.8867 273.44 13.2715C273.001 12.6465 272.459 12.1777 271.814 11.8652C271.17 11.543 270.442 11.3818 269.632 11.3818C268.821 11.3818 268.099 11.543 267.464 11.8652C266.829 12.1777 266.287 12.6465 265.838 13.2715C265.398 13.8867 265.062 14.6533 264.827 15.5713C264.593 16.4795 264.476 17.5293 264.476 18.7207V19.9219C264.476 21.1133 264.593 22.168 264.827 23.0859C265.062 24.0039 265.403 24.7803 265.853 25.415C266.312 26.04 266.858 26.5137 267.493 26.8359C268.128 27.1484 268.851 27.3047 269.661 27.3047C270.481 27.3047 271.209 27.1484 271.844 26.8359C272.479 26.5137 273.016 26.04 273.455 25.415C273.895 24.7803 274.227 24.0039 274.451 23.0859C274.686 22.168 274.803 21.1133 274.803 19.9219ZM294.124 8.67188H297.786V22.9248C297.786 24.5459 297.435 25.9033 296.731 26.9971C296.028 28.0908 295.071 28.916 293.86 29.4727C292.659 30.0195 291.316 30.293 289.832 30.293C288.299 30.293 286.932 30.0195 285.73 29.4727C284.529 28.916 283.582 28.0908 282.889 26.9971C282.205 25.9033 281.863 24.5459 281.863 22.9248V8.67188H285.525V22.9248C285.525 23.9502 285.701 24.7949 286.053 25.459C286.404 26.1133 286.902 26.5967 287.547 26.9092C288.191 27.2217 288.953 27.3779 289.832 27.3779C290.711 27.3779 291.468 27.2217 292.103 26.9092C292.747 26.5967 293.245 26.1133 293.597 25.459C293.948 24.7949 294.124 23.9502 294.124 22.9248V8.67188ZM313.006 24.5068C313.006 24.0674 312.938 23.6768 312.801 23.335C312.674 22.9932 312.444 22.6807 312.112 22.3975C311.78 22.1143 311.312 21.8408 310.706 21.5771C310.11 21.3037 309.349 21.0254 308.421 20.7422C307.405 20.4297 306.468 20.083 305.608 19.7021C304.759 19.3115 304.017 18.8623 303.382 18.3545C302.747 17.8369 302.254 17.2461 301.902 16.582C301.551 15.9082 301.375 15.1318 301.375 14.2529C301.375 13.3838 301.556 12.5928 301.917 11.8799C302.288 11.167 302.811 10.5518 303.484 10.0342C304.168 9.50684 304.974 9.10156 305.901 8.81836C306.829 8.52539 307.854 8.37891 308.978 8.37891C310.56 8.37891 311.922 8.67188 313.064 9.25781C314.217 9.84375 315.101 10.6299 315.716 11.6162C316.341 12.6025 316.653 13.6914 316.653 14.8828H313.006C313.006 14.1797 312.854 13.5596 312.552 13.0225C312.259 12.4756 311.81 12.0459 311.204 11.7334C310.608 11.4209 309.852 11.2646 308.934 11.2646C308.064 11.2646 307.342 11.3965 306.766 11.6602C306.189 11.9238 305.76 12.2803 305.477 12.7295C305.193 13.1787 305.052 13.6865 305.052 14.2529C305.052 14.6533 305.145 15.0195 305.33 15.3516C305.516 15.6738 305.799 15.9766 306.18 16.2598C306.561 16.5332 307.039 16.792 307.615 17.0361C308.191 17.2803 308.87 17.5146 309.651 17.7393C310.833 18.0908 311.863 18.4814 312.742 18.9111C313.621 19.3311 314.354 19.8096 314.939 20.3467C315.525 20.8838 315.965 21.4941 316.258 22.1777C316.551 22.8516 316.697 23.6182 316.697 24.4775C316.697 25.376 316.517 26.1865 316.155 26.9092C315.794 27.6221 315.276 28.2324 314.603 28.7402C313.938 29.2383 313.138 29.624 312.2 29.8975C311.272 30.1611 310.237 30.293 309.095 30.293C308.069 30.293 307.059 30.1562 306.062 29.8828C305.076 29.6094 304.178 29.1943 303.367 28.6377C302.557 28.0713 301.912 27.3682 301.434 26.5283C300.955 25.6787 300.716 24.6875 300.716 23.5547H304.393C304.393 24.248 304.51 24.8389 304.744 25.3271C304.988 25.8154 305.325 26.2158 305.755 26.5283C306.185 26.8311 306.683 27.0557 307.249 27.2021C307.825 27.3486 308.44 27.4219 309.095 27.4219C309.954 27.4219 310.672 27.2998 311.248 27.0557C311.834 26.8115 312.273 26.4697 312.566 26.0303C312.859 25.5908 313.006 25.083 313.006 24.5068ZM333.909 27.085V30H322.586V27.085H333.909ZM323.626 8.67188V30H319.949V8.67188H323.626ZM332.43 17.5781V20.4492H322.586V17.5781H332.43ZM333.836 8.67188V11.6016H322.586V8.67188H333.836Z\" fill=\"white\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t</div>\n" +
                "\t\t<p style=\"position: absolute; width: 368px; height: 33px; left: 45px; top: 112px; font-family: 'Open Sans'; font-style: normal; font-weight: 700; font-size: 24px; line-height: 33px; display: flex; align-items: flex-end; text-left: center; color: #C1FD35;\">Comprovante de depósito</p>\n" +
                "\t\t<p style=\"position: absolute; width: 235px; height: 22px; left: 50px; top: 149px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; text-align: center; color: #EEEAEA;\">TRANSACTION_DATE_TIME</p>\n" +
                "\t\t<div style=\"position: absolute; width: 514px; height: 573px; left: 44px; top: 200px; border-radius: 10px; background: #FFFFFF;\">\n" +
                "\t\t\t<p style=\"position: absolute; width: 103px; height: 22px; left: 30px; top: 21px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Depósito</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 33px; left: 30px; top: 30px; font-family: 'Open Sans'; font-style: normal; font-weight: 700; font-size: 24px; line-height: 33px; display: flex; align-items: flex-end; text-align: left; color: #201F22;\">R$ TRANSACTION_AMOUNT</p>\n" +
                "\t\t\t<svg style=\"position: absolute; width: 453px; height: 0px; left: 30px; top: 89px; border: 1px solid #CECECE;\" width=\"453\" height=\"1\" viewBox=\"0 0 453 1\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<line y1=\"0.5\" x2=\"453\" y2=\"0.5\" stroke=\"#CECECE\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 33px; left: 58px; top: 131px; font-family: 'Open Sans'; font-style: normal; font-weight: 700; font-size: 24px; line-height: 33px; display: flex; align-items: flex-end; color: #201F22;\">CARD_NUMBER</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 242px; height: 22px; left: 58px; top: 170px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Conta:</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 242px; height: 22px; left: 118px; top: 170px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end;\">ACCOUNT_NUMBER</p>\n" +
                "\n" +

                "\t\t\t<svg style=\"position: absolute; left: 33px; top: 119px; border: 1px solid #CECECE;\" width=\"1\" height=\"164\" viewBox=\"0 0 1 164\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<line x1=\"0.5\" y1=\"2.18557e-08\" x2=\"0.499993\" y2=\"164\" stroke=\"#CECECE\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<svg style=\"position: absolute; width: 7px; height: 7px; border-radius: 50%; left: 30px; top: 115px; background: #3A393E; z-index: 2;\" width=\"7\" height=\"7\" viewBox=\"0 0 7 7\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<circle cx=\"3.5\" cy=\"3.5\" r=\"3.5\" fill=\"#3A393E\"/>\n" +
                "\t\t\t</svg>\t\t\t\t\n" +
                "\t\t\t<svg style=\"position: absolute; width: 7px; height: 7px; border-radius: 50%; left: 30px; top: 279px; background: #3A393E; z-index: 2;\" width=\"7\" height=\"7\" viewBox=\"0 0 7 7\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<circle cx=\"3.5\" cy=\"3.5\" r=\"3.5\" fill=\"#3A393E\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<svg style=\"position: absolute; width: 453px; height: 0px; left: 30px; top: 431px; border: 1px solid #CECECE;\" width=\"453\" height=\"1\" viewBox=\"0 0 453 1\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<line y1=\"0.5\" x2=\"453\" y2=\"0.5\" stroke=\"#CECECE\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<p style=\"position: absolute; left: 58px; top: 109px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Cartão</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 105px; height: 22px; left: 58px; top: 432px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Motivo:</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 22px; left: 118px; top: 432px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end;\">DESCRIPTION</p>\n" +
                "\t\t\t<svg style=\"position: absolute; width: 453px; height: 0px; left: 30px; top: 490px; border: 1px solid #CECECE;\" width=\"453\" height=\"1\" viewBox=\"0 0 453 1\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "\t\t\t\t<line y1=\"0.5\" x2=\"453\" y2=\"0.5\" stroke=\"#CECECE\"/>\n" +
                "\t\t\t</svg>\n" +
                "\t\t\t<p style=\"position: absolute; width: 178px; height: 22px; left: 58px; top: 488px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: rgba(0, 0, 0, 0.5);\">Código de depósito</p>\n" +
                "\t\t\t<p style=\"position: absolute; width: 430px; height: 22px; left: 58px; top: 516px; font-family: 'Open Sans'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 22px; display: flex; align-items: flex-end; color: #201F22;\">TRANSACTION_CODE</p>\n" +
                "\t\t\t\t\t\t\t\n" +
                "\t\t</div>\n" +
                "\t</div>";

        String transactionDateTime = Formatter.formatDateTime(depositResponse.getDate());
        String transferenceAmount = Formatter.formatDouble(depositResponse.getAmount());
        String cardNumber = depositResponse.getCardNumber();
        String accountNumber = depositResponse.getAccountNumber();
        String description = depositResponse.getDescription();
        String transferenceCode = depositResponse.getTransactionCode();

        html = html
                .replace("TRANSACTION_DATE_TIME", transactionDateTime)
                .replace("TRANSACTION_AMOUNT", transferenceAmount)
                .replace("CARD_NUMBER", cardNumber)
                .replace("ACCOUNT_NUMBER", accountNumber)
                .replace("DESCRIPTION", (description != null) ? description : "")
                .replace("TRANSACTION_CODE", transferenceCode);

        return html;
    }

    public DocumentContainer generateReport(Account account, YearMonth referenceMonth, List<TransactionResponse> transactions, String contentType) throws IOException, BusinessException, FontFormatException {
        DocumentContainer documentContainer = new DocumentContainer();
        if (contentType == null || contentType.equals("text/csv")) {
            documentContainer = generateCsv(account, referenceMonth, transactions);
        } else if (contentType.equals("application/pdf")) {
            documentContainer = generatePdfFromDocxTemplate(account, referenceMonth, transactions);
        }
        return documentContainer;
    }

    private DocumentContainer generateCsv(Account account, YearMonth referenceMonth, List<TransactionResponse> transactions) throws IOException, BusinessException {
        String month = referenceMonth.toString().replace("-", "_");
        String userAccountNumber = account.getAccountNumber();
        String fileName = String.format("report__%s__%s.csv", month, userAccountNumber);

        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, CSV_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        String[] headerRecord = {"data", "movimento", "tipo", "valor"};
        csvWriter.writeNext(headerRecord);

        for (TransactionResponse transaction : transactions) {
            Boolean isExpense;
            String transactionDate = Formatter.formatDateInDayMonthYear(transaction.getDate().toLocalDate());
            String amount = Formatter.formatDouble(transaction.getAmount()).replace(".", "");
            String transactionDetails = "";
            String transactionType = "ENTRADA";
            if (transaction instanceof TransferenceResponse) {
                TransferenceResponse transference = (TransferenceResponse) transaction;
                isExpense = transference.getOriginAccountNumber().equals(userAccountNumber);
                if (isExpense) {
                    transactionDetails = String.format("Transferência %s", transference.getDestinationAccountHolderName());
                    transactionType = "SAIDA";
                } else {
                    transactionDetails = String.format("Transferência %s", transference.getOriginAccountHolderName());
                }
            }
            if (transaction instanceof DepositResponse) {
                DepositResponse deposit = (DepositResponse) transaction;
                transactionDetails = String.format(
                        "Depósito com cartão %s", deposit.getCardNumber()
                );
            }
            csvWriter.writeNext(new String[] {transactionDate, transactionDetails, transactionType, amount});
            csvWriter.close();
        }

        return new DocumentContainer(writer.toString().getBytes(), fileName);
    }

    private DocumentContainer generatePdfFromDocxTemplate(Account account, YearMonth referenceMonth, List<TransactionResponse> transactions) throws IOException {
        String userAccountNumber = account.getAccountNumber();
        String month = referenceMonth.toString().replace("-", "_");
        String fileName = String.format("report__%s__%s.pdf", month, userAccountNumber);

        if (transactions.isEmpty()) {
            return new DocumentContainer(convertToPdfBytes(getEmptyTemplate(account, referenceMonth)), fileName);
        }

        InputStream reportInputStream = this.getClass().getClassLoader()
                .getResourceAsStream("templates/monthly_report_template.docx");

        XWPFDocument reportTemplate = new XWPFDocument(reportInputStream);

        setDefaultConfig(account, referenceMonth, reportTemplate);

        XWPFTable table = reportTemplate.getTables().get(0);

        for (TransactionResponse transaction : transactions) {
            String amount = Formatter.formatDouble(transaction.getAmount());
            String transactionDate = Formatter.formatDateInDayMonthYear(transaction.getDate().toLocalDate());
            String transactionDetails = "";
            Boolean isExpense;
            String amountColor = "008000";

            if (transaction instanceof TransferenceResponse) {
                TransferenceResponse transference = (TransferenceResponse) transaction;
                isExpense = transference.getOriginAccountNumber().equals(userAccountNumber);
                if (isExpense) {
                    transactionDetails = String.format("Transferência enviada para %s", transference.getDestinationAccountHolderName());
                    amount = "-" + amount;
                    amountColor = "FF0000";
                } else {
                    transactionDetails = String.format("Transferência recebida de %s", transference.getOriginAccountHolderName());
                }
            }
            if (transaction instanceof DepositResponse) {
                DepositResponse deposit = (DepositResponse) transaction;
                transactionDetails = String.format(
                        "Depósito com cartão %s", deposit.getCardNumber()
                );
            }

            XWPFTableRow newRow = table.createRow();
            newRow.setHeight(341);
            XWPFTableCell dateCell = newRow.getCell(0);
            dateCell.setText(transactionDate);
            applyCellStyles(dateCell, ParagraphAlignment.LEFT, XWPFTableCell.XWPFVertAlign.CENTER, "", 12, false);
            XWPFTableCell detailsCell = newRow.getCell(1);
            detailsCell.setText(transactionDetails);
            applyCellStyles(detailsCell, ParagraphAlignment.LEFT, XWPFTableCell.XWPFVertAlign.CENTER, "", 12, false);
            XWPFTableCell amountCell = newRow.getCell(2);
            amountCell.setText(amount);
            applyCellStyles(amountCell, ParagraphAlignment.RIGHT, XWPFTableCell.XWPFVertAlign.CENTER, amountColor, 12, true);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reportTemplate.write(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        reportTemplate.close();
        reportInputStream.close();
        byteArrayOutputStream.close();

        return new DocumentContainer(convertToPdfBytes(bytes), fileName);
    }

    private byte[] getEmptyTemplate(Account account, YearMonth referenceMonth) throws IOException {
        InputStream reportInputStream = this.getClass().getClassLoader()
                .getResourceAsStream("templates/monthly_report_template_no_data.docx");
        XWPFDocument reportTemplate = new XWPFDocument(reportInputStream);

        setDefaultConfig(account, referenceMonth, reportTemplate);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reportTemplate.write(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        reportTemplate.close();
        reportInputStream.close();
        byteArrayOutputStream.close();

        return bytes;
    }

    private void addTabs(XWPFRun run, int tabQuantity) {
        for (int i=0; i < tabQuantity; i++) {
            run.addTab();
        }
    }

    private void setDefaultConfig(Account account, YearMonth referenceMonth, XWPFDocument reportTemplate) {
        String firstDay = Formatter.formatDateInDayMonthYear(referenceMonth.atDay(1));
        String lastDay = Formatter.formatDateInDayMonthYear(referenceMonth.atEndOfMonth());
        LocalDateTime now = Formatter.toBrasiliaTime(LocalDateTime.now());
        String today = Formatter.formatDateInDayMonthYear(now.toLocalDate());
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String nowTime =  String.format("%s:%s:%s", decimalFormat.format(now.getHour()), decimalFormat.format(now.getMinute()), decimalFormat.format(now.getSecond()));
        String userFullName = account.getUserFullName().toUpperCase();
        String userAccountNumber = account.getAccountNumber();
        String userCpf = Formatter.formatCpf(account.getUserCpf());

        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setAscii(FONT_FAMILY);
        fonts.setCs(FONT_FAMILY);
        fonts.setHAnsi(FONT_FAMILY);
        XWPFStyles styles = reportTemplate.createStyles();
        styles.setDefaultFonts(fonts);

        XWPFHeader header = reportTemplate.getHeaderList().get(0);
        XWPFParagraph userFullNameParagraph = header.createParagraph();
        XWPFRun userFullNameRun = userFullNameParagraph.createRun();
        userFullNameRun.setText(userFullName);
        userFullNameParagraph.setSpacingAfter(0);
        XWPFParagraph userInfoParagraph = header.createParagraph();
        XWPFRun userInfoRun = userInfoParagraph.createRun();
        userInfoRun.setText(String.format("CPF: %s", userCpf));
        addTabs(userInfoRun, 3);
        userInfoRun.setText(String.format("CONTA: %s", userAccountNumber));

        XWPFParagraph reportInfoParagraph = reportTemplate.getParagraphs().get(2);
        XWPFRun reportInfoRun = reportInfoParagraph.createRun();
        reportInfoRun.setText(String.format("de %s a %s", firstDay, lastDay));
        addTabs(reportInfoRun, 3);
        reportInfoRun.setText(String.format("Emitido em: %s %s", today, nowTime));
    }

    private void applyCellStyles(XWPFTableCell cell, ParagraphAlignment paragraphAlignment, XWPFTableCell.XWPFVertAlign vertAlign, String textColor, int fontSize, boolean isBold) {
        XWPFRun run = cell.getParagraphs().get(0).getRuns().get(0);
        cell.getParagraphs().get(0).setAlignment(paragraphAlignment);
        cell.setVerticalAlignment(vertAlign);
        run.setColor(textColor);
        run.setFontSize(fontSize);
        run.setBold(isBold);
    };

    private byte[] convertToPdfBytes(byte[] reportBytesFromDoc) {
        Document doc = new Document();
        doc.loadFromStream(new ByteArrayInputStream(reportBytesFromDoc), FileFormat.Docx_2013);

        ToPdfParameterList parameterList =new ToPdfParameterList();
        parameterList.isEmbeddedAllFonts(true);
        parameterList.setDisableLink(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doc.saveToStream(outputStream, parameterList);
        return outputStream.toByteArray();
    }
}
